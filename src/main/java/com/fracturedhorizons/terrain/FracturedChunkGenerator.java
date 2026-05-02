package com.fracturedhorizons.terrain;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import com.fracturedhorizons.config.FracturedConfig;

public class FracturedChunkGenerator extends NoiseBasedChunkGenerator {

    private final Holder<NoiseGeneratorSettings> originalSettings;
    private final FracturedGeneratorSettings fracturedSettings;

    // Mainland underside + edge
    private final ImprovedNoise undersideNoise, edgeNoise;
    // Outer rim Voronoi island system
    private final ImprovedNoise voronoiJitter, islandHeight, islandShape3D, islandTaper;

    public static final MapCodec<FracturedChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(g -> ((FracturedChunkGenerator) g).originalSettings),
            FracturedGeneratorSettings.CODEC.fieldOf("fractured_settings").forGetter(FracturedChunkGenerator::getFracturedSettings)
    ).apply(i, i.stable(FracturedChunkGenerator::new)));

    public FracturedChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings,
                                    FracturedGeneratorSettings fracturedSettings) {
        super(biomeSource, settings);
        this.originalSettings = settings;
        this.fracturedSettings = fracturedSettings;
        this.undersideNoise = new ImprovedNoise(new XoroshiroRandomSource(55629174L));
        this.edgeNoise      = new ImprovedNoise(new XoroshiroRandomSource(19283746L));
        this.voronoiJitter  = new ImprovedNoise(new XoroshiroRandomSource(28476103L));
        this.islandHeight   = new ImprovedNoise(new XoroshiroRandomSource(99182736L));
        this.islandShape3D  = new ImprovedNoise(new XoroshiroRandomSource(36925814L));
        this.islandTaper    = new ImprovedNoise(new XoroshiroRandomSource(84517293L));
    }

    public FracturedGeneratorSettings getFracturedSettings() { return fracturedSettings; }
    @Override protected MapCodec<? extends ChunkGenerator> codec() { return CODEC; }

    @Override
    public void createStructures(RegistryAccess ra, ChunkGeneratorStructureState ss,
                                 StructureManager sm, ChunkAccess c, StructureTemplateManager tm) {
        if (!shouldSuppressStructures(c.getPos()))
            super.createStructures(ra, ss, sm, c, tm);
    }

    private boolean shouldSuppressStructures(ChunkPos cp) {
        if (fracturedSettings.isSkyboundOnly()) return true;
        if (fracturedSettings.isMainlandOnly()) return false;
        return new RadialZoneCalculator().sample(cp.getMiddleBlockX(), cp.getMiddleBlockZ()).zone() != Zone.MAINLAND;
    }

    @Override
    public java.util.concurrent.CompletableFuture<ChunkAccess> fillFromNoise(
            net.minecraft.world.level.levelgen.blending.Blender b, RandomState rs,
            StructureManager sm, ChunkAccess c) {
        return super.fillFromNoise(b, rs, sm, c).thenApply(ch -> { applyZones(ch); return ch; });
    }

    @Override
    public void buildSurface(net.minecraft.server.level.WorldGenRegion r,
                              StructureManager sm, RandomState rs, ChunkAccess c) {
        super.buildSurface(r, sm, rs, c);
        applyZones(c);
    }

    // ================================================================
    //  MAIN
    // ================================================================

    private void applyZones(ChunkAccess chunk) {
        if (fracturedSettings.isMainlandOnly()) return;
        RadialZoneCalculator calc = new RadialZoneCalculator();
        ChunkPos cp = chunk.getPos();
        int minY = chunk.getMinBuildHeight();
        int maxY = chunk.getMaxBuildHeight();
        BlockState air = Blocks.AIR.defaultBlockState();

        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int wx = cp.getMinBlockX() + lx;
                int wz = cp.getMinBlockZ() + lz;

                if (fracturedSettings.isSkyboundOnly()) {
                    clearColumn(chunk, wx, wz, minY, maxY, air);
                    generateVoronoiIsland(chunk, wx, wz, minY, maxY);
                    continue;
                }

                ZoneSample sample = calc.sample(wx, wz);
                switch (sample.zone()) {
                    case MAINLAND:
                        shapeMainland(chunk, wx, wz, sample.t(), minY, maxY, air);
                        break;
                    case BUFFER:
                        clearColumn(chunk, wx, wz, minY, maxY, air);
                        break;
                    case OUTER_RIM:
                        clearColumn(chunk, wx, wz, minY, maxY, air);
                        generateVoronoiIsland(chunk, wx, wz, minY, maxY);
                        break;
                }
            }
        }
    }

    // ================================================================
    //  MAINLAND — soft height cap + PRESERVED underside
    // ================================================================

    private void shapeMainland(ChunkAccess chunk, int wx, int wz, double t,
                                int minY, int maxY, BlockState air) {
        // --- SOFT upper-envelope limiter ---
        // Only shaves peaks that exceed the cap. Never forces terrain onto a shelf.
        // Inner 92%: no cap — vanilla terrain is fully free
        // Outer 8%: soft quadratic reduction of allowed peak height
        double maxPeakHeight;
        if (t < 0.92) {
            maxPeakHeight = 400.0; // effectively uncapped
        } else {
            double edgeT = (t - 0.92) / 0.08; // 0→1 across last 8%
            maxPeakHeight = 400.0 - 240.0 * edgeT * edgeT; // 400→160 at rim
        }

        // --- PRESERVED underside logic (do not change) ---
        double baseFloor = -100.0 + 100.0 * t;
        double floorNoise = undersideNoise.noise(wx / 40.0, 0, wz / 40.0) * 20.0;
        double floor = baseFloor + floorNoise;

        if (t > 0.9) {
            double edgeFactor = (t - 0.9) / 0.1; // narrow 10% edge zone
            double cliffNoise = edgeNoise.noise(wx / 20.0, 0, wz / 20.0) * 15.0;
            floor += edgeFactor * 40.0 + cliffNoise * edgeFactor;
        }

        int floorY = Math.max((int) floor, minY);
        int capY = Math.min((int) maxPeakHeight, maxY);

        // --- Find surface first, then decide what to do ---
        int surfaceY = minY;
        for (int y = Math.min(maxY - 1, 400); y >= minY; y--) {
            if (!chunk.getBlockState(new BlockPos(wx, y, wz)).isAir()) {
                surfaceY = y;
                break;
            }
        }

        // --- Apply ---
        for (int y = minY; y < maxY; y++) {
            BlockPos pos = new BlockPos(wx, y, wz);

            // HEIGHT CAP: only if this column's surface exceeds the limit
            // Shave from the top down. Don't touch anything at or below cap.
            if (surfaceY > capY && y > capY) {
                BlockState state = chunk.getBlockState(pos);
                if (!state.isAir()) {
                    double aboveCap = y - capY;
                    double capNoise = edgeNoise.noise(wx / 15.0, y / 12.0, wz / 15.0);
                    // Organic shaving: more blocks removed further above cap
                    if (capNoise < aboveCap * 0.1 - 0.4) {
                        // Keep block (small peak poking above)
                    } else {
                        chunk.setBlockState(pos, air, false);
                    }
                }
            } else if (y < floorY) {
                // PRESERVED: Below floor = void
                chunk.setBlockState(pos, air, false);
            } else if (y < floorY + 15) {
                // PRESERVED: Underside taper zone
                BlockState state = chunk.getBlockState(pos);
                if (!state.isAir()) {
                    double taperNoise = undersideNoise.noise(wx / 12.0, y / 10.0, wz / 12.0);
                    double taperProgress = (double)(y - floorY) / 15.0;
                    if (taperNoise > taperProgress * 1.2 - 0.2) {
                        chunk.setBlockState(pos, air, false);
                    }
                }
            }

            // Edge water cleanup (narrow rim only)
            if (t > 0.9 && y >= floorY && y < capY) {
                BlockState state = chunk.getBlockState(pos);
                if (!state.getFluidState().isEmpty()) {
                    if (y > capY - 4)
                        chunk.setBlockState(pos, Blocks.GRASS_BLOCK.defaultBlockState(), false);
                    else if (y > 0)
                        chunk.setBlockState(pos, Blocks.STONE.defaultBlockState(), false);
                    else
                        chunk.setBlockState(pos, Blocks.DEEPSLATE.defaultBlockState(), false);
                }
            }
        }
    }

    // ================================================================
    //  OUTER RIM — Voronoi cell-based isolated islands
    //  Buffer exclusion applied at island CENTER, not post-clipping.
    // ================================================================

    private void generateVoronoiIsland(ChunkAccess chunk, int wx, int wz,
                                        int minY, int maxY) {
        int floorY = FracturedConfig.ISLAND_FLOOR_Y.get();
        int ceilY = FracturedConfig.ISLAND_CEIL_Y.get();
        double outerRimStart = FracturedConfig.OUTER_RIM_START.get();

        int cellSize = 500; // Large cells = bigger islands, wide spacing

        int gridX = Math.floorDiv(wx, cellSize);
        int gridZ = Math.floorDiv(wz, cellSize);

        // Voronoi search: SKIP centers inside buffer during search itself
        // This prevents buffer-interior centers from distorting cell boundaries
        double nearest = Double.MAX_VALUE;
        double secondNearest = Double.MAX_VALUE;
        double nearCX = 0, nearCZ = 0;

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                int cx = gridX + dx;
                int cz = gridZ + dz;

                double jx = (cx + 0.5) * cellSize
                        + voronoiJitter.noise(cx * 0.7, 0.0, cz * 0.7) * cellSize * 0.35;
                double jz = (cz + 0.5) * cellSize
                        + voronoiJitter.noise(cx * 0.7, 100.0, cz * 0.7) * cellSize * 0.35;

                // PRE-EXCLUSION: skip this center if it falls inside buffer/mainland
                double centerDist = Math.sqrt(jx * jx + jz * jz);
                if (centerDist < outerRimStart) continue;

                double dist = Math.sqrt((wx - jx) * (wx - jx) + (wz - jz) * (wz - jz));

                if (dist < nearest) {
                    secondNearest = nearest;
                    nearest = dist;
                    nearCX = jx;
                    nearCZ = jz;
                } else if (dist < secondNearest) {
                    secondNearest = dist;
                }
            }
        }

        // No valid island centers found nearby
        if (nearest == Double.MAX_VALUE) return;
        if (secondNearest == Double.MAX_VALUE) secondNearest = nearest * 2.0;

        double edgeFactor = 1.0 - (nearest / secondNearest);

        double crackWidth = 0.30; // wide void gaps
        if (edgeFactor < crackWidth) return;

        double islandFactor = Math.min((edgeFactor - crackWidth) / (1.0 - crackWidth), 1.0);

        // Per-island SIZE VARIATION (wider range: 0.3x to 1.8x)
        double sizeVar = islandHeight.noise(nearCX / 150.0, 50.0, nearCZ / 150.0);
        double sizeMult = Math.max(1.0 + sizeVar * 1.6, 0.3);

        double heightVar = islandHeight.noise(nearCX / 300.0, 0, nearCZ / 300.0);
        int islandBaseY = floorY + (int)(heightVar * 90.0);
        int verticalExtent = (int)((60 + islandFactor * 90) * sizeMult);
        int islandTopY = Math.min(ceilY, islandBaseY + verticalExtent);

        int rMin = Math.max(islandBaseY, minY);
        int rMax = Math.min(islandTopY, maxY);
        if (rMin >= rMax) return;

        // --- 3D shape: dome top, tapered bottom ---
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState deepslate = Blocks.DEEPSLATE.defaultBlockState();
        BlockState dirt = Blocks.DIRT.defaultBlockState();
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();

        boolean[] solid = new boolean[rMax - rMin];
        for (int y = rMin; y < rMax; y++) {
            int idx = y - rMin;
            double yNorm = (double)(y - islandBaseY) / Math.max(islandTopY - islandBaseY, 1);

            double verticalDensity;
            if (yNorm > 0.6) {
                double topT = (yNorm - 0.6) / 0.4;
                verticalDensity = 1.0 - topT * topT;
            } else {
                double botT = yNorm / 0.6;
                verticalDensity = botT * botT;
            }

            double density = verticalDensity * islandFactor;
            double rough = islandShape3D.noise(wx / 50.0, y / 35.0, wz / 50.0) * 0.12;
            density += rough;

            solid[idx] = density > 0.18;
        }

        for (int y = rMin; y < rMax; y++) {
            int idx = y - rMin;
            if (!solid[idx]) continue;

            boolean isTop = (idx + 1 >= solid.length || !solid[idx + 1]);
            int toSurface = 0;
            for (int a = idx + 1; a < solid.length && solid[a]; a++) toSurface++;

            BlockState b;
            if (isTop) b = grass;
            else if (toSurface <= 3) b = dirt;
            else if (y < islandBaseY + 12) b = deepslate;
            else b = stone;

            chunk.setBlockState(new BlockPos(wx, y, wz), b, false);
        }
    }

    // ================================================================

    private void clearColumn(ChunkAccess chunk, int wx, int wz, int minY, int maxY, BlockState air) {
        for (int y = minY; y < maxY; y++)
            chunk.setBlockState(new BlockPos(wx, y, wz), air, false);
    }
}

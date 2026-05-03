package com.fracturedhorizons.registry;

import com.fracturedhorizons.terrain.DistanceHeightFalloffFunction;
import com.fracturedhorizons.terrain.RadialZoneFunction;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ApexNoiseRouterData {

    private static NoiseRouter buildRouter(
            DensityFunction finalDensity,
            DensityFunction depth,
            DensityFunction initialDensityWithoutJaggedness,
            HolderGetter<NormalNoise.NoiseParameters> noiseParams) {

        DensityFunction temp    = DensityFunctions.noise(noiseParams.getOrThrow(Noises.TEMPERATURE), 0.25, 0.25);
        DensityFunction veg     = DensityFunctions.noise(noiseParams.getOrThrow(Noises.VEGETATION), 0.25, 0.25);
        DensityFunction cont    = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CONTINENTALNESS), 0.25, 0.25);
        DensityFunction ero     = DensityFunctions.noise(noiseParams.getOrThrow(Noises.EROSION), 0.25, 0.25);
        DensityFunction ridges  = DensityFunctions.noise(noiseParams.getOrThrow(Noises.RIDGE), 0.25, 0.25);

        return new NoiseRouter(
                DensityFunctions.zero(), // barrier
                DensityFunctions.zero(), // fluidLevelFloodedness
                DensityFunctions.zero(), // fluidLevelSpread
                DensityFunctions.zero(), // lava
                temp,
                veg,
                cont,
                ero,
                depth,
                ridges,
                // initialDensityWithoutJaggedness: steuert abovePreliminarySurface() für Surface Rules.
                // Negativ = oberhalb der geschätzten Oberfläche → Grass-Regeln feuern.
                // Positiv = unterhalb = Deepslate-Zone. Wir steuern Deepslate manuell in apexSurfaceRules().
                initialDensityWithoutJaggedness,
                finalDensity,
                DensityFunctions.zero(), // veinToggle
                DensityFunctions.zero(), // veinRidged
                DensityFunctions.zero()  // veinGap
        );
    }

    // initialDensityWithoutJaggedness für Mainland/Shattered:
    // Y-Gradient minus großem Offset → ab Y≈30 stets negativ → abovePreliminarySurface() immer true
    // → Grass-Regeln feuern auf der gesamten Oberfläche. Deepslate wird manuell in apexSurfaceRules() gesetzt.
    private static DensityFunction buildInitialDensity() {
        return DensityFunctions.add(
                DensityFunctions.yClampedGradient(-128, 512, 1.0, -2.0),
                DensityFunctions.constant(-1.5)
        );
    }

    // Gemeinsames Terrain-Noise für alle Apex-Presets (JAGGED + CONTINENTALNESS).
    private static DensityFunction buildBaseTerrain(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction jagged = DensityFunctions.noise(noiseParams.getOrThrow(Noises.JAGGED), 1.0, 1.0);
        DensityFunction continent = DensityFunctions.mul(
                DensityFunctions.noise(noiseParams.getOrThrow(Noises.CONTINENTALNESS), 0.25, 0.25),
                DensityFunctions.constant(0.8)
        );
        // Y-Gradient: Y=-128 → 1.0, Y=512 → -2.0
        // Durchschnittliche Oberfläche bei ~Y=85; Berge mit kombiniertem Max-Noise bis ~Y=400
        DensityFunction yGrad = DensityFunctions.yClampedGradient(-128, 512, 1.0, -2.0);
        return DensityFunctions.add(yGrad, DensityFunctions.add(jagged, continent));
    }

    // Drei schwebende Insel-Bänder (RIDGE-Noise) für Zone 4.
    private static DensityFunction buildIslandBands(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction ridge = DensityFunctions.noise(noiseParams.getOrThrow(Noises.RIDGE), 1.0, 0.5);

        // Band 1: Low-Orbit Y=64-160 (Zentrum Y=112)
        DensityFunction band1 = DensityFunctions.add(
                DensityFunctions.add(
                        DensityFunctions.min(
                                DensityFunctions.yClampedGradient(64, 112, -2.0, 2.0),
                                DensityFunctions.yClampedGradient(112, 160, 2.0, -2.0)
                        ),
                        ridge
                ),
                DensityFunctions.constant(0.4)
        );

        // Band 2: Cloud-Layer Y=200-340 (Zentrum Y=270)
        DensityFunction band2 = DensityFunctions.add(
                DensityFunctions.add(
                        DensityFunctions.min(
                                DensityFunctions.yClampedGradient(200, 270, -2.0, 2.0),
                                DensityFunctions.yClampedGradient(270, 340, 2.0, -2.0)
                        ),
                        ridge
                ),
                DensityFunctions.constant(0.3)
        );

        // Band 3: High-Apex Y=400-600 (Zentrum Y=500)
        DensityFunction band3 = DensityFunctions.add(
                DensityFunctions.add(
                        DensityFunctions.min(
                                DensityFunctions.yClampedGradient(400, 500, -2.0, 2.0),
                                DensityFunctions.yClampedGradient(500, 600, 2.0, -2.0)
                        ),
                        ridge
                ),
                DensityFunctions.constant(0.2)
        );

        return DensityFunctions.max(band1, DensityFunctions.max(band2, band3));
    }

    public static NoiseRouter mainlandRouter(BootstrapContext<?> context, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction baseTerrain = buildBaseTerrain(noiseParams);
        return buildRouter(baseTerrain, baseTerrain, buildInitialDensity(), noiseParams);
    }

    public static NoiseRouter skyboundOnlyRouter(BootstrapContext<?> context, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction allIslands = buildIslandBands(noiseParams);
        DensityFunction depth = DensityFunctions.yClampedGradient(-128, 512, 1.0, -2.0);
        // Inseln haben keine kontinuierliche Oberfläche → initialDensity immer negativ = abovePreliminarySurface immer true
        return buildRouter(allIslands, depth, DensityFunctions.constant(-1.0), noiseParams);
    }

    public static NoiseRouter shatteredLandsRouter(BootstrapContext<?> context, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction baseTerrain = buildBaseTerrain(noiseParams);

        // Zonengrenzen: Zone 1 = 0-20k, Zone 2 = 20k-28k, Zone 3 = 28k-32k, Zone 4 = 32k+
        // RadialZoneFunction(32000): gibt dist/32000 zurück, max 1.0
        // 20k/32k = 0.625 (Ende Zone 1), 28k/32k = 0.875 (Ende Zone 2)
        RadialZoneFunction radialZone = new RadialZoneFunction(32000.0);

        // mainlandFade: 1.0 im Zentrum (≤20k), 0.0 ab 28k+ — Smoothstep-Kurve
        DistanceHeightFalloffFunction mainlandFade =
                new DistanceHeightFalloffFunction(radialZone, 0.625, 0.875);

        // voidFactor = (1 - mainlandFade): 0.0 im Zentrum, 1.0 ab 28k+
        DensityFunction voidFactor = DensityFunctions.add(
                DensityFunctions.constant(1.0),
                DensityFunctions.mul(mainlandFade, DensityFunctions.constant(-1.0))
        );

        // Höhen-Subtraktion: zieht Terrain über Y=64 mit wachsender Distanz nach unten
        DensityFunction falloffGrad = DensityFunctions.yClampedGradient(64, 512, 0.0, -5.0);
        DensityFunction heightSub = DensityFunctions.mul(falloffGrad, voidFactor);
        DensityFunction adjustedMainland = DensityFunctions.add(baseTerrain, heightSub);

        // Mainland nur in Zone 1-3 (dist < 32k): bei Zone 4 → void
        DensityFunction maskedMainland = DensityFunctions.rangeChoice(
                radialZone, 1.0, 1.5,
                DensityFunctions.constant(-100.0),
                adjustedMainland
        );

        // Inseln nur in Zone 4 (dist >= 32k, normiert = 1.0)
        DensityFunction allIslands = buildIslandBands(noiseParams);
        DensityFunction activeIslands = DensityFunctions.rangeChoice(
                radialZone, 1.0, 1.5,
                allIslands,
                DensityFunctions.constant(-100.0)
        );

        DensityFunction finalDensity = DensityFunctions.max(maskedMainland, activeIslands);

        return buildRouter(finalDensity, baseTerrain, buildInitialDensity(), noiseParams);
    }
}

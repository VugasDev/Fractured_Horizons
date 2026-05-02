package com.fracturedhorizons.terrain;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

import java.util.Map;
import java.util.stream.Stream;

public class FracturedBiomeSource extends BiomeSource {

    public static final MapCodec<FracturedBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    MultiNoiseBiomeSource.CODEC.fieldOf("wrapped").forGetter(FracturedBiomeSource::getWrapped)
            ).apply(instance, FracturedBiomeSource::new));

    // Hardcoded ocean/river -> land biome replacements
    private static final Map<String, String> AQUATIC_REPLACEMENTS = Map.ofEntries(
            Map.entry("ocean", "plains"),
            Map.entry("deep_ocean", "plains"),
            Map.entry("warm_ocean", "savanna"),
            Map.entry("lukewarm_ocean", "forest"),
            Map.entry("deep_lukewarm_ocean", "forest"),
            Map.entry("cold_ocean", "taiga"),
            Map.entry("deep_cold_ocean", "taiga"),
            Map.entry("frozen_ocean", "snowy_plains"),
            Map.entry("deep_frozen_ocean", "snowy_plains"),
            Map.entry("river", "plains"),
            Map.entry("frozen_river", "snowy_plains"));

    private final MultiNoiseBiomeSource wrapped;
    private final RadialZoneCalculator calculator = new RadialZoneCalculator();

    public FracturedBiomeSource(MultiNoiseBiomeSource wrapped) {
        this.wrapped = wrapped;
    }

    public MultiNoiseBiomeSource getWrapped() { return wrapped; }

    @Override
    protected MapCodec<? extends BiomeSource> codec() { return CODEC; }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return wrapped.possibleBiomes().stream();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        Holder<Biome> original = wrapped.getNoiseBiome(quartX, quartY, quartZ, sampler);
        int blockX = QuartPos.toBlock(quartX);
        int blockZ = QuartPos.toBlock(quartZ);
        ZoneSample sample = calculator.sample(blockX, blockZ);

        if (sample.zone() == Zone.MAINLAND) return original;

        // Buffer + outer rim: replace aquatic biomes with land equivalents
        String biomePath = original.unwrapKey().map(k -> k.location().getPath()).orElse("");
        String replacement = AQUATIC_REPLACEMENTS.get(biomePath);

        if (replacement != null) {
            // Find the replacement biome from the wrapped source's possible biomes
            ResourceLocation replaceLoc = ResourceLocation.withDefaultNamespace(replacement);
            for (Holder<Biome> candidate : wrapped.possibleBiomes()) {
                if (candidate.unwrapKey().map(k -> k.location().equals(replaceLoc)).orElse(false)) {
                    return candidate;
                }
            }
        }

        return original;
    }
}

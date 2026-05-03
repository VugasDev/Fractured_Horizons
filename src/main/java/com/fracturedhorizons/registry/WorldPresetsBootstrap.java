package com.fracturedhorizons.registry;

import com.fracturedhorizons.preset.FracturedWorldPresets;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Map;

public class WorldPresetsBootstrap {

    public static void bootstrap(BootstrapContext<WorldPreset> context) {
        HolderGetter<DimensionType> dimensionTypes = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> noiseSettings = context.lookup(Registries.NOISE_SETTINGS);

        Holder<DimensionType> apexDimType = dimensionTypes.getOrThrow(ApexDimensionTypeBootstrap.APEX_DIM_TYPE);

        MultiNoiseBiomeSource biomeSource = MultiNoiseBiomeSource.createFromPreset(
                context.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)
                       .getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD)
        );

        context.register(FracturedWorldPresets.SHATTERED_LANDS, new WorldPreset(Map.of(
                LevelStem.OVERWORLD, new LevelStem(apexDimType,
                        new NoiseBasedChunkGenerator(biomeSource,
                                noiseSettings.getOrThrow(ApexNoiseSettingsBootstrap.SHATTERED_LANDS_SETTINGS)))
        )));

        context.register(FracturedWorldPresets.SKYBOUND_ONLY, new WorldPreset(Map.of(
                LevelStem.OVERWORLD, new LevelStem(apexDimType,
                        new NoiseBasedChunkGenerator(biomeSource,
                                noiseSettings.getOrThrow(ApexNoiseSettingsBootstrap.SKYBOUND_ONLY_SETTINGS)))
        )));

        context.register(FracturedWorldPresets.MAINLAND_ONLY, new WorldPreset(Map.of(
                LevelStem.OVERWORLD, new LevelStem(apexDimType,
                        new NoiseBasedChunkGenerator(biomeSource,
                                noiseSettings.getOrThrow(ApexNoiseSettingsBootstrap.MAINLAND_ONLY_SETTINGS)))
        )));

        // Standard Modded: vanilla DimensionType + vanilla NoiseGeneratorSettings.
        // Der WorldDimensionsMixin greift hier NICHT ein (kein apex_type), Terralith/Tectonic
        // übernehmen die Weltgenerierung wie gewohnt.
        Holder<DimensionType> vanillaDimType = dimensionTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD);
        Holder<NoiseGeneratorSettings> vanillaNoiseSettings = noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD);

        context.register(FracturedWorldPresets.STANDARD_MODDED, new WorldPreset(Map.of(
                LevelStem.OVERWORLD, new LevelStem(vanillaDimType,
                        new NoiseBasedChunkGenerator(biomeSource, vanillaNoiseSettings))
        )));
    }
}

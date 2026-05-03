package com.fracturedhorizons.registry;

import com.fracturedhorizons.FracturedHorizonsMod;
import com.fracturedhorizons.terrain.FracturedBiomeSource;
import com.fracturedhorizons.terrain.FracturedChunkGenerator;
import com.fracturedhorizons.terrain.DistanceHeightFalloffFunction;
import com.fracturedhorizons.terrain.FracturedDensityFunction;
import com.fracturedhorizons.terrain.RadialZoneFunction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FracturedRegistries {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = 
            DeferredRegister.create(Registries.CHUNK_GENERATOR, FracturedHorizonsMod.MODID);

    public static final Supplier<MapCodec<FracturedChunkGenerator>> FRACTURED_GENERATOR = 
            CHUNK_GENERATORS.register("fractured_generator", () -> FracturedChunkGenerator.CODEC);

    public static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTIONS = 
            DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, FracturedHorizonsMod.MODID);

    public static final Supplier<MapCodec<RadialZoneFunction>> RADIAL_ZONE = 
            DENSITY_FUNCTIONS.register("radial_zone", () -> RadialZoneFunction.CODEC);

    public static final Supplier<MapCodec<DistanceHeightFalloffFunction>> DISTANCE_FALLOFF = 
            DENSITY_FUNCTIONS.register("distance_falloff", () -> DistanceHeightFalloffFunction.CODEC);

    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(Registries.BIOME_SOURCE, FracturedHorizonsMod.MODID);

    public static final Supplier<MapCodec<FracturedBiomeSource>> FRACTURED_BIOME_SOURCE =
            BIOME_SOURCES.register("fractured_biome_source", () -> FracturedBiomeSource.CODEC);

    public static void register(IEventBus bus) {
        CHUNK_GENERATORS.register(bus);
        DENSITY_FUNCTIONS.register(bus);
        BIOME_SOURCES.register(bus);
    }
}

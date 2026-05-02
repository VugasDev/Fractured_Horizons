package com.fracturedhorizons.preset;

import com.fracturedhorizons.FracturedHorizonsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class FracturedWorldPresets {
    public static final ResourceKey<WorldPreset> SHATTERED_LANDS = createKey("shattered_lands");
    public static final ResourceKey<WorldPreset> SKYBOUND_ONLY = createKey("skybound_only");
    public static final ResourceKey<WorldPreset> MAINLAND_ONLY = createKey("mainland_only");

    private static ResourceKey<WorldPreset> createKey(String name) {
        return ResourceKey.create(Registries.WORLD_PRESET, ResourceLocation.fromNamespaceAndPath(FracturedHorizonsMod.MODID, name));
    }
}

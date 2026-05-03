package com.fracturedhorizons.registry;

import com.fracturedhorizons.FracturedHorizonsMod;
import com.fracturedhorizons.preset.FracturedWorldPresets;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.WorldPresetTagsProvider;
import net.minecraft.tags.WorldPresetTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ApexWorldPresetTagsProvider extends WorldPresetTagsProvider {

    public ApexWorldPresetTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper) {
        super(output, provider, FracturedHorizonsMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Füge unsere eigenen Presets zum "normal" Tag hinzu.
        // Minecraft rendert im UI (und über die Game Rules) nur WorldPresets, die in "minecraft:normal" oder "minecraft:extended" liegen.
        this.tag(WorldPresetTags.NORMAL).add(
                FracturedWorldPresets.SHATTERED_LANDS,
                FracturedWorldPresets.SKYBOUND_ONLY,
                FracturedWorldPresets.MAINLAND_ONLY,
                FracturedWorldPresets.STANDARD_MODDED
        );
    }
}

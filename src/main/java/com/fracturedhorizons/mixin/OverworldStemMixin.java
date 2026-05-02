package com.fracturedhorizons.mixin;

import com.fracturedhorizons.config.FracturedConfig;
import com.fracturedhorizons.terrain.FracturedChunkGenerator;
import com.fracturedhorizons.terrain.FracturedGeneratorSettings;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftServer.class)
public class OverworldStemMixin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @ModifyVariable(
        method = "createLevels",
        at = @At(value = "STORE")
    )
    private LevelStem injectFracturedGenerator(LevelStem originalStem) {
        if (originalStem != null && originalStem.generator() instanceof NoiseBasedChunkGenerator noiseGen) {
            // Target the overworld noise settings specifically to avoid touching Nether/End or other custom dims
            if (noiseGen.generatorSettings().is(NoiseGeneratorSettings.OVERWORLD)) {
                LOGGER.info("[FracturedHorizons] FracturedGenerator activated for Overworld! Wrapping original biome source.");
                
                boolean isSkybound = FracturedConfig.IS_SKYBOUND_ONLY.get();
                boolean isMainland = FracturedConfig.IS_MAINLAND_ONLY.get();
                boolean isShattered = !isSkybound && !isMainland;
                
                FracturedGeneratorSettings fracSettings = new FracturedGeneratorSettings(
                    isShattered, 
                    isSkybound, 
                    isMainland
                );

                FracturedChunkGenerator fracturedGenerator = new FracturedChunkGenerator(
                    noiseGen.getBiomeSource(),
                    noiseGen.generatorSettings(),
                    fracSettings
                );
                
                // Get the shattered_type from the dynamic registry to apply our custom heights (-128 to 896)
                net.minecraft.core.Registry<net.minecraft.world.level.dimension.DimensionType> dimRegistry = 
                    ((MinecraftServer)(Object)this).registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DIMENSION_TYPE);
                
                net.minecraft.resources.ResourceKey<net.minecraft.world.level.dimension.DimensionType> shatteredKey = 
                    net.minecraft.resources.ResourceKey.create(
                        net.minecraft.core.registries.Registries.DIMENSION_TYPE, 
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("fractured_horizons", "shattered_type")
                    );
                
                net.minecraft.core.Holder<net.minecraft.world.level.dimension.DimensionType> newType = originalStem.type();
                java.util.Optional<net.minecraft.core.Holder.Reference<net.minecraft.world.level.dimension.DimensionType>> opt = dimRegistry.getHolder(shatteredKey);
                if (opt.isPresent()) {
                    newType = opt.get();
                    LOGGER.info("[FracturedHorizons] Successfully applied custom DimensionType (shattered_type) to Overworld. Limits: minY={}, height={}", newType.value().minY(), newType.value().height());
                } else {
                    LOGGER.warn("[FracturedHorizons] Could not find shattered_type DimensionType! Falling back to vanilla limits.");
                }

                return new LevelStem(newType, fracturedGenerator);
            }
        }
        return originalStem;
    }
}

package com.fracturedhorizons;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import com.fracturedhorizons.config.FracturedConfig;
import org.slf4j.Logger;

@Mod(FracturedHorizonsMod.MODID)
public class FracturedHorizonsMod {
    public static final String MODID = "fractured_horizons";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FracturedHorizonsMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("[FracturedHorizons] Mod constructor called — registering config and registries...");
        modContainer.registerConfig(ModConfig.Type.COMMON, FracturedConfig.SPEC);
        
        com.fracturedhorizons.registry.FracturedRegistries.register(modEventBus);
        LOGGER.info("[FracturedHorizons] Registration complete. World presets should appear in world creation screen.");
    }
}

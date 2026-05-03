package com.fracturedhorizons;

import com.fracturedhorizons.config.FracturedConfig;
import com.fracturedhorizons.registry.FracturedRegistries;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(FracturedHorizonsMod.MODID)
public class FracturedHorizonsMod {
    public static final String MODID = "fractured_horizons";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FracturedHorizonsMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, FracturedConfig.SPEC);
        FracturedRegistries.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(FracturedHorizonsMod::onServerStarting);
    }

    private static void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        var overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            LOGGER.warn("[FracturedHorizons] Overworld is null on ServerStarting!");
            return;
        }

        ChunkGenerator gen = overworld.getChunkSource().getGenerator();
        String genType = gen.getClass().getSimpleName();

        // DimensionType prüfen
        DimensionType dimType = overworld.dimensionType();
        int minY = dimType.minY();
        int height = dimType.height();
        int logicalHeight = dimType.logicalHeight();

        LOGGER.info("[FracturedHorizons] === WORLD GENERATOR DIAGNOSTIC ===");
        LOGGER.info("[FracturedHorizons] ChunkGenerator: {}", genType);
        LOGGER.info("[FracturedHorizons] DimensionType: minY={}, height={}, logicalHeight={}", minY, height, logicalHeight);

        if (minY == -128 && height == 896) {
            LOGGER.info("[FracturedHorizons] ✔ Fractured Horizons DimensionType aktiv (apex_type)!");
        } else {
            LOGGER.warn("[FracturedHorizons] ✘ VANILLA DimensionType aktiv! minY={} height={}", minY, height);
            LOGGER.warn("[FracturedHorizons] Bitte beim Erstellen einer neuen Welt: 'Welttyp' auf 'Shattered Lands', 'Skybound Only' oder 'Mainland Only' setzen!");
        }

        // WorldPreset-Registry auf unsere Einträge prüfen
        var registryAccess = server.registryAccess();
        var presetRegistry = registryAccess.registryOrThrow(Registries.WORLD_PRESET);
        boolean hasShattered = presetRegistry.containsKey(ResourceLocation.fromNamespaceAndPath(MODID, "shattered_lands"));
        boolean hasSkybound = presetRegistry.containsKey(ResourceLocation.fromNamespaceAndPath(MODID, "skybound_only"));
        boolean hasMainland = presetRegistry.containsKey(ResourceLocation.fromNamespaceAndPath(MODID, "mainland_only"));
        LOGGER.info("[FracturedHorizons] Presets im Registry: shattered_lands={}, skybound_only={}, mainland_only={}", hasShattered, hasSkybound, hasMainland);

        var dimTypeRegistry = registryAccess.registryOrThrow(Registries.DIMENSION_TYPE);
        boolean hasApexType = dimTypeRegistry.containsKey(ResourceLocation.fromNamespaceAndPath(MODID, "apex_type"));
        LOGGER.info("[FracturedHorizons] DimensionType apex_type im Registry: {}", hasApexType);
        LOGGER.info("[FracturedHorizons] === END DIAGNOSTIC ===");
    }
}

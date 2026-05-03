package com.fracturedhorizons.terrain;

import com.fracturedhorizons.FracturedHorizonsMod;
import com.fracturedhorizons.mixin.NoiseBasedChunkGeneratorAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

import java.util.Map;

/**
 * Injiziert Terralith-kompatible Surface Rules in die Apex-Noise-Settings zur Server-Startzeit.
 *
 * Problem: SurfaceRuleData.overworld() enthält nur Biome-Checks für vanilla-Biome (minecraft:*).
 * Terralith ersetzt alle Overworld-Biome durch eigene (terralith:*), deren Surface Rules in
 * data/minecraft/worldgen/noise_settings/overworld.json liegen. Diese Datei überschreibt die
 * vanilla minecraft:overworld Noise-Settings, aber nicht unsere fractured_horizons:*-Settings.
 *
 * Fix: Wir lesen zur Startzeit die geladenen minecraft:overworld Noise-Settings (die durch Terralith
 * bereits erweitert wurden) und ersetzen Zone D in unseren Apex-Settings durch diese Rule.
 */
public class ApexSurfaceRulesInjector {

    public static void inject(MinecraftServer server) {
        var registryAccess = server.registryAccess();

        // minecraft:overworld Noise-Settings lesen — enthalten nach Datapack-Laden Terralit's Rules
        Registry<NoiseGeneratorSettings> noiseRegistry;
        try {
            noiseRegistry = registryAccess.registryOrThrow(Registries.NOISE_SETTINGS);
        } catch (Exception e) {
            FracturedHorizonsMod.LOGGER.warn("[FracturedHorizons] Noise-Settings-Registry nicht verfügbar: {}", e.getMessage());
            return;
        }

        NoiseGeneratorSettings overworldSettings = noiseRegistry.get(NoiseGeneratorSettings.OVERWORLD);
        SurfaceRules.RuleSource zoneD;
        if (overworldSettings != null) {
            zoneD = overworldSettings.surfaceRule();
            FracturedHorizonsMod.LOGGER.info("[FracturedHorizons] Verwende minecraft:overworld Surface Rules (inkl. Terralith-Biome) als Zone D");
        } else {
            // Fallback: vanilla SurfaceRuleData wenn minecraft:overworld nicht verfügbar
            zoneD = SurfaceRuleData.overworld();
            FracturedHorizonsMod.LOGGER.warn("[FracturedHorizons] minecraft:overworld Noise-Settings nicht gefunden, nutze vanilla Fallback");
        }

        SurfaceRules.RuleSource patchedSurfaceRule = buildApexSurfaceRules(zoneD);

        // LevelStem-Registry nach Apex-Dimensionen durchsuchen
        Registry<LevelStem> levelStemRegistry;
        try {
            levelStemRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        } catch (Exception e) {
            FracturedHorizonsMod.LOGGER.warn("[FracturedHorizons] LevelStem-Registry nicht verfügbar: {}", e.getMessage());
            return;
        }

        int patchCount = 0;
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : levelStemRegistry.entrySet()) {
            LevelStem stem = entry.getValue();
            if (!(stem.generator() instanceof NoiseBasedChunkGenerator noiseGen)) continue;

            // Nur unsere Apex-Settings patchen (Namespace check)
            Holder<NoiseGeneratorSettings> settingsHolder = ((NoiseBasedChunkGeneratorAccessor) noiseGen).apex$getSettings();
            var settingsKey = settingsHolder.unwrapKey();
            if (settingsKey.isEmpty()) continue;
            if (!settingsKey.get().location().getNamespace().equals(FracturedHorizonsMod.MODID)) continue;

            NoiseGeneratorSettings current = settingsHolder.value();

            NoiseGeneratorSettings patched = new NoiseGeneratorSettings(
                current.noiseSettings(),
                current.defaultBlock(),
                current.defaultFluid(),
                current.noiseRouter(),
                patchedSurfaceRule,
                current.spawnTarget(),
                current.seaLevel(),
                current.disableMobGeneration(),
                current.isAquifersEnabled(),
                current.oreVeinsEnabled(),
                current.useLegacyRandomSource()
            );

            ((NoiseBasedChunkGeneratorAccessor) noiseGen).apex$setSettings(Holder.direct(patched));
            patchCount++;
            FracturedHorizonsMod.LOGGER.info("[FracturedHorizons] Surface Rules gepatcht für: {}",
                entry.getKey().location());
        }

        if (patchCount > 0) {
            FracturedHorizonsMod.LOGGER.info("[FracturedHorizons] {} Apex-Dimension(en) mit Terralith-Surface-Rules gepatcht", patchCount);
        } else {
            FracturedHorizonsMod.LOGGER.debug("[FracturedHorizons] Keine Apex-Dimensionen im aktiven World-Preset gefunden (normal bei vanilla Welt)");
        }
    }

    /**
     * Baut die Apex-spezifischen Surface Rules mit der übergebenen Zone D (meist minecraft:overworld Rules).
     *
     * Struktur:
     *   Zone A (y < -96):  Stochastic Bedrock + Blackstone
     *   Zone B (y -96..-64): Blackstone + ~25% scattered Magma via GRAVEL-Noise
     *   Zone C (y -64..0):   Deepslate (explizit, da abovePreliminarySurface deaktiviert)
     *   Zone D (y >= 0):     zoneD-Parameter (vanilla + Terralith Biom-Surface-Rules)
     */
    public static SurfaceRules.RuleSource buildApexSurfaceRules(SurfaceRules.RuleSource zoneD) {
        // Zone A: Bedrock-Gradient (world bottom bis Y=-96) + Blackstone
        SurfaceRules.RuleSource zoneA = SurfaceRules.ifTrue(
            SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(-96), 0)),
            SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                    SurfaceRules.verticalGradient("apex_bedrock_floor",
                        VerticalAnchor.bottom(),
                        VerticalAnchor.absolute(-96)
                    ),
                    SurfaceRules.state(Blocks.BEDROCK.defaultBlockState())
                ),
                SurfaceRules.state(Blocks.BLACKSTONE.defaultBlockState())
            )
        );

        // Zone B: Blackstone mit ~25% Magma-Einstreuung (via GRAVEL-Noise) — Y -96 bis -64
        SurfaceRules.RuleSource zoneB = SurfaceRules.ifTrue(
            SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(-64), 0)),
            SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                    SurfaceRules.noiseCondition(Noises.GRAVEL, -0.35, 0.2),
                    SurfaceRules.state(Blocks.MAGMA_BLOCK.defaultBlockState())
                ),
                SurfaceRules.state(Blocks.BLACKSTONE.defaultBlockState())
            )
        );

        // Zone C: Deepslate von Y -64 bis 0
        SurfaceRules.RuleSource zoneC = SurfaceRules.ifTrue(
            SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(0), 0)),
            SurfaceRules.state(Blocks.DEEPSLATE.defaultBlockState())
        );

        return SurfaceRules.sequence(zoneA, zoneB, zoneC, zoneD);
    }
}

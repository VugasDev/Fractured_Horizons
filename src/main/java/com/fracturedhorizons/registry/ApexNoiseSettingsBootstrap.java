package com.fracturedhorizons.registry;

import com.fracturedhorizons.FracturedHorizonsMod;
import com.fracturedhorizons.terrain.ApexSurfaceRulesInjector;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.List;

public class ApexNoiseSettingsBootstrap {

    public static final ResourceKey<NoiseGeneratorSettings> SHATTERED_LANDS_SETTINGS = createKey("shattered_lands_settings");
    public static final ResourceKey<NoiseGeneratorSettings> SKYBOUND_ONLY_SETTINGS    = createKey("skybound_only_settings");
    public static final ResourceKey<NoiseGeneratorSettings> MAINLAND_ONLY_SETTINGS    = createKey("mainland_only_settings");

    private static ResourceKey<NoiseGeneratorSettings> createKey(String name) {
        return ResourceKey.create(Registries.NOISE_SETTINGS,
                ResourceLocation.fromNamespaceAndPath(FracturedHorizonsMod.MODID, name));
    }

    public static void bootstrap(BootstrapContext<NoiseGeneratorSettings> context) {
        context.register(MAINLAND_ONLY_SETTINGS,    createMainlandSettings(context));
        context.register(SHATTERED_LANDS_SETTINGS,  createShatteredLandsSettings(context));
        context.register(SKYBOUND_ONLY_SETTINGS,    createSkyboundOnlySettings(context));
    }

    // Zone D beim Bootstrap: vanilla Fallback.
    // ApexSurfaceRulesInjector ersetzt Zone D zur Laufzeit durch die geladenen
    // minecraft:overworld-Rules (inkl. Terralith-Biome), sobald der Server startet.
    private static SurfaceRules.RuleSource apexSurfaceRules() {
        return ApexSurfaceRulesInjector.buildApexSurfaceRules(SurfaceRuleData.overworld());
    }

    private static NoiseGeneratorSettings createMainlandSettings(BootstrapContext<NoiseGeneratorSettings> context) {
        NoiseSettings noiseSettings = NoiseSettings.create(-128, 896, 1, 2);
        HolderGetter<NormalNoise.NoiseParameters> noiseParams = context.lookup(Registries.NOISE);
        NoiseRouter router = ApexNoiseRouterData.mainlandRouter(context, noiseParams);

        return new NoiseGeneratorSettings(
                noiseSettings,
                Blocks.STONE.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                router,
                apexSurfaceRules(),
                List.of(),
                64,    // seaLevel
                false, // disableMobGeneration
                false, // aquifersEnabled
                true,  // oreVeinsEnabled
                false  // useLegacyRandom
        );
    }

    private static NoiseGeneratorSettings createShatteredLandsSettings(BootstrapContext<NoiseGeneratorSettings> context) {
        NoiseSettings noiseSettings = NoiseSettings.create(-128, 896, 1, 2);
        HolderGetter<NormalNoise.NoiseParameters> noiseParams = context.lookup(Registries.NOISE);
        NoiseRouter router = ApexNoiseRouterData.shatteredLandsRouter(context, noiseParams);

        return new NoiseGeneratorSettings(
                noiseSettings,
                Blocks.STONE.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                router,
                apexSurfaceRules(),
                List.of(),
                64,
                false,
                false,
                true,
                false
        );
    }

    private static NoiseGeneratorSettings createSkyboundOnlySettings(BootstrapContext<NoiseGeneratorSettings> context) {
        NoiseSettings noiseSettings = NoiseSettings.create(-128, 896, 1, 2);
        HolderGetter<NormalNoise.NoiseParameters> noiseParams = context.lookup(Registries.NOISE);
        NoiseRouter router = ApexNoiseRouterData.skyboundOnlyRouter(context, noiseParams);

        return new NoiseGeneratorSettings(
                noiseSettings,
                Blocks.STONE.defaultBlockState(),
                // AIR statt WATER: verhindert unendliches Fluid-Spreading in der void-Welt
                // (Server-Thread-Deadlock wenn FlowingFluid.tick() Chunks in den Void laden will)
                Blocks.AIR.defaultBlockState(),
                router,
                apexSurfaceRules(),
                List.of(),
                -128,  // seaLevel tief genug, dass keine Wasseroberfläche entsteht
                false,
                false,
                true,
                false
        );
    }
}

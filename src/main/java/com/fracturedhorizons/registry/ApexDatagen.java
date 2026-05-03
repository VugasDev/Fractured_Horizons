package com.fracturedhorizons.registry;

import com.fracturedhorizons.FracturedHorizonsMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = FracturedHorizonsMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ApexDatagen {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        RegistrySetBuilder builder = new RegistrySetBuilder()
                .add(Registries.DENSITY_FUNCTION, ApexDensityFunctionsBootstrap::bootstrap)
                .add(Registries.NOISE_SETTINGS, ApexNoiseSettingsBootstrap::bootstrap)
                .add(Registries.DIMENSION_TYPE, ApexDimensionTypeBootstrap::bootstrap)
                .add(Registries.WORLD_PRESET, WorldPresetsBootstrap::bootstrap);

        // 1. Erstelle die JSONs für die Datapack Registries
        DatapackBuiltinEntriesProvider datapackProvider = new DatapackBuiltinEntriesProvider(
                output,
                lookupProvider,
                builder,
                Set.of(FracturedHorizonsMod.MODID)
        );
        generator.addProvider(event.includeServer(), datapackProvider);

        // 2. Tags! Damit die Presets im UI sichtbar werden.
        // Das MUSS auf lookupProvider von DatapackBuiltinEntriesProvider warten, 
        // weil die Tags die vorher generierten Presets referenzieren!
        generator.addProvider(event.includeServer(), new ApexWorldPresetTagsProvider(
                output, 
                datapackProvider.getRegistryProvider(), 
                event.getExistingFileHelper()
        ));
    }
}

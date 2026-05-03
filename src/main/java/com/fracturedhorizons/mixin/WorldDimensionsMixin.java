package com.fracturedhorizons.mixin;

import com.fracturedhorizons.registry.ApexDimensionTypeBootstrap;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

// Gibt unserem WorldPreset Priorität über datapackDimensions (z.B. Terralith's overworld.json).
// WorldDimensions.bake() bevorzugt stemRegistry (aus "data/{mod}/dimension/overworld.json")
// über this.dimensions. Terralith liefert genau so eine Datei mit vanilla DimensionType
// und überschreibt damit unsere apex_type-Einstellungen.
// Fix: Wenn apex_type im Preset erkannt wird, gewinnt das Preset über Datapack-Einträge.
@Mixin(WorldDimensions.class)
public class WorldDimensionsMixin {

    @Inject(method = "bake", at = @At("HEAD"), cancellable = true)
    private void apex$prioritizePresetOverDatapack(
        Registry<LevelStem> stemRegistry,
        CallbackInfoReturnable<WorldDimensions.Complete> cir
    ) {
        Map<ResourceKey<LevelStem>, LevelStem> presetDimensions = ((WorldDimensions)(Object)this).dimensions();

        LevelStem ourOverworld = presetDimensions.get(LevelStem.OVERWORLD);
        if (ourOverworld == null || !ourOverworld.type().is(ApexDimensionTypeBootstrap.APEX_DIM_TYPE)) {
            return;
        }

        // apex_type erkannt → Preset hat Priorität über datapackDimensions (Terralith etc.)
        Stream<ResourceKey<LevelStem>> allKeys = Stream.concat(
            stemRegistry.registryKeySet().stream(),
            presetDimensions.keySet().stream()
        ).distinct();

        WritableRegistry<LevelStem> writableRegistry = new MappedRegistry<>(Registries.LEVEL_STEM, Lifecycle.experimental());

        WorldDimensions.keysInOrder(allKeys).forEach(key -> {
            // Preset zuerst, dann Datapack (umgekehrt zu vanilla bake())
            Optional<LevelStem> stem = Optional.ofNullable(presetDimensions.get(key))
                .or(() -> stemRegistry.getOptional(key));

            stem.ifPresent(s -> writableRegistry.register(
                key, s,
                new RegistrationInfo(Optional.empty(), Lifecycle.experimental())
            ));
        });

        Registry<LevelStem> frozenRegistry = writableRegistry.freeze();
        cir.setReturnValue(new WorldDimensions.Complete(frozenRegistry, PrimaryLevelData.SpecialWorldProperty.NONE));
        cir.cancel();
    }
}

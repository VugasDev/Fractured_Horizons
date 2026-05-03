package com.fracturedhorizons.registry;

import com.fracturedhorizons.FracturedHorizonsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.OptionalLong;

/**
 * ApexDimensionTypeBootstrap: Registriert den Custom DimensionType für das Apex-Weltgenerierungssystem.
 * Konfiguration: minY=-128, height=896 (entspricht 56 Chunks Gesamthöhe).
 */
public class ApexDimensionTypeBootstrap {
    
    public static final ResourceKey<DimensionType> APEX_DIM_TYPE = ResourceKey.create(
            Registries.DIMENSION_TYPE, 
            ResourceLocation.fromNamespaceAndPath(FracturedHorizonsMod.MODID, "apex_type")
    );

    public static void bootstrap(BootstrapContext<DimensionType> context) {
        context.register(APEX_DIM_TYPE, new DimensionType(
                OptionalLong.empty(),           // fixedTime: None
                true,                           // hasSkylight: Yes
                false,                          // hasCeiling: No
                false,                          // ultraWarm: No
                true,                           // natural: Yes
                1.0,                            // coordinateScale: 1:1
                true,                           // bedWorks: Yes
                false,                          // respawnAnchorWorks: No
                -128,                           // minY: -128
                896,                            // height: 896
                768,                            // logicalHeight: 768
                BlockTags.INFINIBURN_OVERWORLD, // infiniburn: Vanilla Overworld
                ResourceLocation.withDefaultNamespace("overworld"),// effectsLocation: Sky/Fog like Overworld
                0.0f,                           // ambientLight
                new DimensionType.MonsterSettings(false, true, UniformInt.of(0, 7), 0)
        ));
    }
}

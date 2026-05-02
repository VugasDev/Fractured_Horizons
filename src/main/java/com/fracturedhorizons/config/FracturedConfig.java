package com.fracturedhorizons.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class FracturedConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue MAINLAND_RADIUS;
    public static final ModConfigSpec.DoubleValue OUTER_RIM_START;

    // Island generation parameters
    public static final ModConfigSpec.DoubleValue ISLAND_NOISE_SCALE;
    public static final ModConfigSpec.DoubleValue ISLAND_THRESHOLD;
    public static final ModConfigSpec.IntValue ISLAND_FLOOR_Y;
    public static final ModConfigSpec.IntValue ISLAND_CEIL_Y;

    // World Types
    public static final ModConfigSpec.BooleanValue IS_SHATTERED;
    public static final ModConfigSpec.BooleanValue IS_SKYBOUND_ONLY;
    public static final ModConfigSpec.BooleanValue IS_MAINLAND_ONLY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("zones");
        MAINLAND_RADIUS = builder
                .comment("Radius of the main floating island (blocks from origin).",
                         "DEBUG DEFAULT: 512 blocks for quick testing.",
                         "Production suggestion: 10000+")
                .defineInRange("mainlandRadius", 512.0, 100.0, 1000000.0);

        OUTER_RIM_START = builder
                .comment("Distance from origin where outer-rim floating islands begin.",
                         "The gap between mainlandRadius and this value is the buffer strip.",
                         "DEBUG DEFAULT: 768 blocks (256 block buffer).",
                         "Production suggestion: 12000+")
                .defineInRange("outerRimStart", 768.0, 200.0, 1000000.0);
        builder.pop();

        builder.push("islands");
        ISLAND_NOISE_SCALE = builder
                .comment("Scale of noise for outer-rim island generation.",
                         "Larger values = bigger islands with bigger gaps.")
                .defineInRange("islandNoiseScale", 200.0, 10.0, 10000.0);

        ISLAND_THRESHOLD = builder
                .comment("Noise threshold for island vs void.",
                         "Higher = fewer, smaller islands.")
                .defineInRange("islandThreshold", 0.08, -1.0, 1.0);

        ISLAND_FLOOR_Y = builder
                .comment("Lowest Y for floating islands.")
                .defineInRange("islandFloorY", 40, -128, 768);

        ISLAND_CEIL_Y = builder
                .comment("Maximum height cap for procedural islands.")
                .defineInRange("islandCeilY", 320, 64, 512);
        builder.pop();

        builder.push("world_type");
        IS_SHATTERED = builder
                .comment("Standard mixed mode: Mainland at center, buffer void, then procedural islands.")
                .define("isShattered", true);
        
        IS_SKYBOUND_ONLY = builder
                .comment("Procedural islands everywhere, no mainland.")
                .define("isSkyboundOnly", false);
                
        IS_MAINLAND_ONLY = builder
                .comment("Only the mainland is generated. Void everywhere else.")
                .define("isMainlandOnly", false);
        builder.pop();

        SPEC = builder.build();
    }
}

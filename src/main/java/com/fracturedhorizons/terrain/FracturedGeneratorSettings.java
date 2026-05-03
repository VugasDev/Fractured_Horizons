package com.fracturedhorizons.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FracturedGeneratorSettings(
        boolean isShattered,
        boolean isSkyboundOnly,
        boolean isMainlandOnly,
        double mainlandRadius,
        double outerRimStart,
        int islandFloorY,
        int islandCeilY,
        double islandThreshold,
        double islandCrackWidth,
        int islandCellSize
) {
    public static final Codec<FracturedGeneratorSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("is_shattered", true).forGetter(FracturedGeneratorSettings::isShattered),
            Codec.BOOL.optionalFieldOf("is_skybound_only", false).forGetter(FracturedGeneratorSettings::isSkyboundOnly),
            Codec.BOOL.optionalFieldOf("is_mainland_only", false).forGetter(FracturedGeneratorSettings::isMainlandOnly),
            Codec.DOUBLE.optionalFieldOf("mainland_radius", 512.0).forGetter(FracturedGeneratorSettings::mainlandRadius),
            Codec.DOUBLE.optionalFieldOf("outer_rim_start", 768.0).forGetter(FracturedGeneratorSettings::outerRimStart),
            Codec.INT.optionalFieldOf("island_floor_y", 40).forGetter(FracturedGeneratorSettings::islandFloorY),
            Codec.INT.optionalFieldOf("island_ceil_y", 320).forGetter(FracturedGeneratorSettings::islandCeilY),
            Codec.DOUBLE.optionalFieldOf("island_threshold", 0.18).forGetter(FracturedGeneratorSettings::islandThreshold),
            Codec.DOUBLE.optionalFieldOf("island_crack_width", 0.30).forGetter(FracturedGeneratorSettings::islandCrackWidth),
            Codec.INT.optionalFieldOf("island_cell_size", 500).forGetter(FracturedGeneratorSettings::islandCellSize)
    ).apply(instance, FracturedGeneratorSettings::new));
    
    // Convenience constructor for existing code
    public FracturedGeneratorSettings(boolean isShattered, boolean isSkyboundOnly, boolean isMainlandOnly) {
        this(isShattered, isSkyboundOnly, isMainlandOnly, 512.0, 768.0, 40, 320, 0.18, 0.30, 500);
    }
}

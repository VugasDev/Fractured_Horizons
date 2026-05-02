package com.fracturedhorizons.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FracturedGeneratorSettings(boolean isShattered, boolean isSkyboundOnly, boolean isMainlandOnly) {
    public static final Codec<FracturedGeneratorSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("is_shattered").forGetter(FracturedGeneratorSettings::isShattered),
            Codec.BOOL.fieldOf("is_skybound_only").forGetter(FracturedGeneratorSettings::isSkyboundOnly),
            Codec.BOOL.fieldOf("is_mainland_only").forGetter(FracturedGeneratorSettings::isMainlandOnly)
    ).apply(instance, FracturedGeneratorSettings::new));
}

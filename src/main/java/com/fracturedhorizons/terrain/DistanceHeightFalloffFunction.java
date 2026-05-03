package com.fracturedhorizons.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * DistanceHeightFalloffFunction: Gibt einen Multiplikator (1.0 -> 0.0) basierend auf einer Eingangs-Distanz zurück.
 * Wird verwendet, um die Geländehöhe an den Rändern einer Zone auszufaden.
 */
public record DistanceHeightFalloffFunction(DensityFunction distanceInput, double falloffStart, double falloffEnd) implements DensityFunction {

    public static final MapCodec<DistanceHeightFalloffFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(DistanceHeightFalloffFunction::distanceInput),
            Codec.DOUBLE.fieldOf("falloff_start").forGetter(DistanceHeightFalloffFunction::falloffStart),
            Codec.DOUBLE.fieldOf("falloff_end").forGetter(DistanceHeightFalloffFunction::falloffEnd)
    ).apply(instance, DistanceHeightFalloffFunction::new));

    public static final KeyDispatchDataCodec<DistanceHeightFalloffFunction> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);

    @Override
    public double compute(FunctionContext context) {
        double dist = distanceInput.compute(context);
        
        if (dist <= falloffStart) {
            return 1.0;
        }
        if (dist >= falloffEnd) {
            return 0.0;
        }
        
        double t = (falloffEnd - dist) / (falloffEnd - falloffStart);
        // Smoothstep: t² * (3 - 2t)
        return t * t * (3.0 - 2.0 * t);
    }

    @Override
    public void fillArray(double[] array, ContextProvider contextProvider) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.compute(contextProvider.forIndex(i));
        }
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return visitor.apply(new DistanceHeightFalloffFunction(distanceInput.mapAll(visitor), falloffStart, falloffEnd));
    }

    @Override
    public double minValue() {
        return 0.0;
    }

    @Override
    public double maxValue() {
        return 1.0;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return DATA_CODEC;
    }
}

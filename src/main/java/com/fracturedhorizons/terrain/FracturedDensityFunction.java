package com.fracturedhorizons.terrain;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Density function for the Fractured Horizons terrain model:
 * - MAINLAND: one giant floating landmass. Height cap shrinks from ~320 at center
 *   to ~60 at edge. Floor rises from ~-100 at center to ~-20 at edge. Both soft-faded.
 * - BUFFER: pure void (air). Guarantees visual separation.
 * - OUTER_RIM: pure void (air). Chunk generator places floating islands.
 */
public class FracturedDensityFunction implements DensityFunction {

    public static final MapCodec<FracturedDensityFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(FracturedDensityFunction::input),
            FracturedGeneratorSettings.CODEC.fieldOf("settings").forGetter(FracturedDensityFunction::settings)
    ).apply(instance, FracturedDensityFunction::new));

    public static final KeyDispatchDataCodec<FracturedDensityFunction> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);

    private final DensityFunction input;
    private final FracturedGeneratorSettings settings;
    private final RadialZoneCalculator calculator;

    public FracturedDensityFunction(DensityFunction input, FracturedGeneratorSettings settings) {
        this.input = input;
        this.settings = settings;
        this.calculator = new RadialZoneCalculator();
    }

    public DensityFunction input() { return input; }
    public FracturedGeneratorSettings settings() { return settings; }

    @Override
    public double compute(FunctionContext context) {
        int x = context.blockX();
        int y = context.blockY();
        int z = context.blockZ();

        if (settings.isMainlandOnly()) return input.compute(context);
        if (settings.isSkyboundOnly()) return -1.0;

        ZoneSample sample = calculator.sample(x, z);

        // BUFFER + OUTER RIM: pure air
        if (sample.zone() != Zone.MAINLAND) return -1.0;

        // MAINLAND: floating landmass with height gradient
        double distRatio = sample.t(); // 0 at center, 1 at edge

        // Height cap: 320 at center → 60 at edge
        double maxHeight = 320.0 - (320.0 - 60.0) * distRatio;

        // Floor: -100 at center → -20 at edge (makes it a floating mass)
        double floorHeight = -100.0 + (100.0 - 20.0) * distRatio;

        double original = input.compute(context);

        // Soft fade above maxHeight — terrain dissolves into air
        if (y > maxHeight) {
            double fade = (y - maxHeight) * 0.06;
            return original - fade;
        }

        // Soft fade below floor — terrain dissolves into void underneath
        if (y < floorHeight) {
            double fade = (floorHeight - y) * 0.06;
            return original - fade;
        }

        return original;
    }

    @Override
    public void fillArray(double[] array, ContextProvider contextProvider) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = compute(contextProvider.forIndex(i));
        }
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return visitor.apply(new FracturedDensityFunction(input.mapAll(visitor), settings));
    }

    @Override public double minValue() { return -1.0; }
    @Override public double maxValue() { return input.maxValue(); }
    @Override public KeyDispatchDataCodec<? extends DensityFunction> codec() { return DATA_CODEC; }
}

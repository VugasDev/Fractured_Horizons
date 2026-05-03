package com.fracturedhorizons.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * RadialZoneFunction: Berechnet sqrt(x² + z²), gibt einen normierten Distanzwert (0.0 bis 1.0) zurück.
 * Werte oberhalb des Radius werden auf 1.0 begrenzt.
 * Internes 2D-Caching optimiert die Performance bei vertikalen Samples (identische X/Z Koordinaten).
 */
public record RadialZoneFunction(double radius) implements DensityFunction {

    public static final MapCodec<RadialZoneFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("radius").forGetter(RadialZoneFunction::radius)
    ).apply(instance, RadialZoneFunction::new));

    public static final KeyDispatchDataCodec<RadialZoneFunction> DATA_CODEC = KeyDispatchDataCodec.of(CODEC);

    // ThreadLocal Cache für Thread-Sicherheit bei gleichzeitiger Chunk-Generierung
    private static final ThreadLocal<Cache> CACHE = ThreadLocal.withInitial(Cache::new);

    private static class Cache {
        int lastX = Integer.MIN_VALUE;
        int lastZ = Integer.MIN_VALUE;
        double lastValue;
    }

    @Override
    public double compute(FunctionContext context) {
        int x = context.blockX();
        int z = context.blockZ();

        Cache cache = CACHE.get();
        if (x == cache.lastX && z == cache.lastZ) {
            return cache.lastValue;
        }

        double distance = Math.sqrt((double) x * x + (double) z * z);
        // Schutz vor Division durch Null
        double safeRadius = Math.max(1.0, radius);
        double value = Math.min(1.0, distance / safeRadius);

        cache.lastX = x;
        cache.lastZ = z;
        cache.lastValue = value;

        return value;
    }

    @Override
    public void fillArray(double[] array, ContextProvider contextProvider) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = this.compute(contextProvider.forIndex(i));
        }
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return visitor.apply(this);
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

    // --- Einfacher Testfall ---
    public static void main(String[] args) {
        RadialZoneFunction func = new RadialZoneFunction(100.0);
        System.out.println("Starte RadialZoneFunction Tests...");

        assertTest(func, 0, 0, 0.0, "Zentrum");
        assertTest(func, 100, 0, 1.0, "Radius-Grenze");
        assertTest(func, 50, 0, 0.5, "Halbe Distanz");
        assertTest(func, 200, 0, 1.0, "Außerhalb (Clamped)");
        
        // Cache Test (X, Z gleich, Y unterschiedlich)
        double val1 = func.compute(new TestContext(10, 0, 20));
        double val2 = func.compute(new TestContext(10, 50, 20));
        if (val1 != val2) throw new AssertionError("2D Cache Fehler!");

        System.out.println("Alle Tests erfolgreich bestanden!");
    }

    private static void assertTest(RadialZoneFunction func, int x, int z, double expected, String label) {
        double actual = func.compute(new TestContext(x, 0, z));
        if (Math.abs(actual - expected) > 1e-6) {
            throw new AssertionError(label + " fehlgeschlagen: Erwartet " + expected + ", erhalten " + actual);
        }
        System.out.println("OK: " + label + " (" + actual + ")");
    }

    private record TestContext(int blockX, int blockY, int blockZ) implements FunctionContext {}
}

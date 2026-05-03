package com.fracturedhorizons.terrain;

public class RadialZoneCalculator {

    public ZoneSample sample(int blockX, int blockZ, double mainlandRadius, double outerRimStart) {
        double dist = Math.sqrt((double) blockX * blockX + (double) blockZ * blockZ);

        if (dist <= mainlandRadius) {
            // t = distance ratio within mainland (0 at center, 1 at edge)
            double t = mainlandRadius > 0 ? dist / mainlandRadius : 1.0;
            return new ZoneSample(Zone.MAINLAND, dist, t, t);
        }
        if (dist < outerRimStart) {
            // Buffer strip: empty void between mainland and outer rim
            double denom = outerRimStart - mainlandRadius;
            double t = denom > 0 ? (dist - mainlandRadius) / denom : 1.0;
            return new ZoneSample(Zone.BUFFER, dist, t, t);
        }
        // Outer rim
        return new ZoneSample(Zone.OUTER_RIM, dist, 1.0, 1.0);
    }
}

package com.fracturedhorizons.terrain;

import com.fracturedhorizons.config.FracturedConfig;

public class RadialZoneCalculator {

    public ZoneSample sample(int blockX, int blockZ) {
        double dist = Math.sqrt((double) blockX * blockX + (double) blockZ * blockZ);

        double mainlandRadius = FracturedConfig.MAINLAND_RADIUS.get();
        double outerRimStart = FracturedConfig.OUTER_RIM_START.get();

        if (dist <= mainlandRadius) {
            // t = distance ratio within mainland (0 at center, 1 at edge)
            double t = dist / mainlandRadius;
            return new ZoneSample(Zone.MAINLAND, dist, t, t);
        }
        if (dist < outerRimStart) {
            // Buffer strip: empty void between mainland and outer rim
            double t = (dist - mainlandRadius) / (outerRimStart - mainlandRadius);
            return new ZoneSample(Zone.BUFFER, dist, t, t);
        }
        // Outer rim
        return new ZoneSample(Zone.OUTER_RIM, dist, 1.0, 1.0);
    }
}

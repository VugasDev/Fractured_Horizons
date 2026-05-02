package com.fracturedhorizons.terrain;

// MAINLAND = giant floating landmass (center to mainlandRadius)
// BUFFER = empty void strip (mainlandRadius to outerRimStart)
// OUTER_RIM = isolated floating island clusters (beyond outerRimStart)
public enum Zone {
    MAINLAND, BUFFER, OUTER_RIM
}

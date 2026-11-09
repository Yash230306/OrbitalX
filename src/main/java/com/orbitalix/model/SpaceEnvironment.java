package com.orbitalix.model;

public record SpaceEnvironment(double radiation, double solarFlux, double debrisMultiplier,
                               boolean radiationStorm, boolean inSunlight) {
    public SpaceEnvironment {
        if (!Double.isFinite(radiation) || radiation < 0 || !Double.isFinite(solarFlux) || solarFlux < 0
                || !Double.isFinite(debrisMultiplier) || debrisMultiplier < 0) {
            throw new IllegalArgumentException("Environment values must be finite and non-negative");
        }
    }

    public static SpaceEnvironment nominal(double radiationBase, double debrisMultiplier) {
        return new SpaceEnvironment(radiationBase, 1, debrisMultiplier, false, true);
    }
}

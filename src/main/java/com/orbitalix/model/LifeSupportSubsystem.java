package com.orbitalix.model;

import com.orbitalix.api.FaultType;
import com.orbitalix.sensor.AtmosphereSensor;
import com.orbitalix.sensor.PressureSensor;
import java.util.List;

public final class LifeSupportSubsystem extends Subsystem {
    public LifeSupportSubsystem(Spacecraft owner) {
        super("Life Support", List.of(new PressureSensor("cabin-pressure", owner::pressure),
                new AtmosphereSensor("cabin-co2", owner::co2)));
    }
    @Override protected void applyPhysics(Spacecraft craft, SpaceEnvironment environment) {
        double oxygenUse = craft.livingCrewCount() * 0.2;
        if (hasFault(FaultType.HULL_BREACH)) {
            oxygenUse *= 1.6;
            craft.leakPressure(2);
        }
        craft.consumeOxygen(oxygenUse);
        craft.addCo2(0.05);
        if (!hasFault(FaultType.SCRUBBER_FAILURE) || craft.backupScrubberActive()) craft.scrubCo2(0.08);
    }
    @Override public LifeSupportSubsystem copy(Spacecraft newOwner) {
        LifeSupportSubsystem copy = new LifeSupportSubsystem(newOwner); copyFaultsTo(copy); return copy;
    }
}

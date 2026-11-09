package com.orbitalix.model;

import com.orbitalix.api.FaultType;
import com.orbitalix.sensor.VoltageSensor;
import java.util.List;

public final class PowerSubsystem extends Subsystem {
    public PowerSubsystem(Spacecraft owner) {
        super("Power", List.of(new VoltageSensor("bus-voltage", owner::battery)));
    }
    @Override protected void applyPhysics(Spacecraft craft, SpaceEnvironment environment) {
        double generation = hasFault(FaultType.SOLAR_FAILURE) ? 0
                : (craft.solarPanelsReoriented() ? 0.3 : 0.6) * environment.solarFlux();
        double load = 0.2 + 0.3 * craft.throttle() / 100.0 + (craft.nonEssentialSystemsOn() ? 0.2 : 0);
        craft.updateBattery(generation - load);
        if (craft.battery() < craft.limits().get(com.orbitalix.api.LimitType.MIN_BATTERY)) craft.setNonEssentialSystemsOn(false);
    }
    @Override public PowerSubsystem copy(Spacecraft newOwner) {
        PowerSubsystem copy = new PowerSubsystem(newOwner); copyFaultsTo(copy); return copy;
    }
}

package com.orbitalix.model;

import com.orbitalix.api.FaultType;
import com.orbitalix.sensor.TemperatureSensor;
import java.util.List;

public final class ThermalSubsystem extends Subsystem {
    public ThermalSubsystem(Spacecraft owner) {
        super("Thermal", List.of(new TemperatureSensor("cabin-temperature", owner::temperature)));
    }
    @Override protected void applyEnvironment(Spacecraft craft, SpaceEnvironment environment) {
        if (hasFault(FaultType.COOLANT_FAILURE)) craft.setCoolantWorking(false);
    }
    @Override protected void applyPhysics(Spacecraft craft, SpaceEnvironment environment) {
        double target = 20 + 0.75 * craft.throttle() + (craft.nonEssentialSystemsOn() ? 5 : 0);
        if (craft.radiatorsActive() && craft.coolantWorking()) target -= 15;
        if (hasFault(FaultType.OVERHEAT)) target += 17;
        craft.updateTemperature(target);
    }
    @Override public ThermalSubsystem copy(Spacecraft newOwner) {
        ThermalSubsystem copy = new ThermalSubsystem(newOwner); copyFaultsTo(copy); return copy;
    }
}

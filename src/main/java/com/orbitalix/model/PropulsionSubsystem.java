package com.orbitalix.model;

import com.orbitalix.api.FaultType;
import com.orbitalix.sensor.TemperatureSensor;
import java.util.List;

public final class PropulsionSubsystem extends Subsystem {
    private final Spacecraft owner;
    public PropulsionSubsystem(Spacecraft owner) {
        super("Propulsion", List.of(new TemperatureSensor("engine-temperature", owner::temperature)));
        this.owner = owner;
    }
    @Override protected void applyPhysics(Spacecraft craft, SpaceEnvironment environment) {
        double throttle = craft.throttle() / 100.0;
        double burn = (0.1 + 1.4 * throttle * throttle) * craft.config().difficulty().burnMultiplier();
        if (hasFault(FaultType.FUEL_LEAK)) burn += 0.45;
        craft.burnFuel(burn);
        craft.setEngineEfficiency(craft.backupEngineActive() ? 0.85 : hasFault(FaultType.ENGINE_MISFIRE) ? 0.55 : 1.0);
    }
    @Override public PropulsionSubsystem copy(Spacecraft newOwner) {
        PropulsionSubsystem copy = new PropulsionSubsystem(newOwner); copyFaultsTo(copy); return copy;
    }
    public Spacecraft owner() { return owner; }
}

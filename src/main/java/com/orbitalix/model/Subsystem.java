package com.orbitalix.model;

import com.orbitalix.api.Copyable;
import com.orbitalix.api.FaultType;
import com.orbitalix.api.HealthStatus;
import com.orbitalix.sensor.Sensor;
import java.util.*;

/** Template Method subsystem with encapsulated sensors and runtime fault state. */
public abstract class Subsystem implements SystemComponent, Copyable<Subsystem> {
    private final String name;
    private final List<Sensor> sensors;
    private final EnumSet<FaultType> faults = EnumSet.noneOf(FaultType.class);
    private HealthStatus health = HealthStatus.NOMINAL;

    protected Subsystem(String name, List<? extends Sensor> sensors) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Subsystem name is required");
        this.name = name;
        this.sensors = new ArrayList<>(sensors);
    }

    @Override public final String name() { return name; }
    public final synchronized List<Sensor> sensors() { return List.copyOf(sensors); }
    @Override public final synchronized List<SystemComponent> children() { return List.copyOf(sensors); }
    @Override public final synchronized HealthStatus health() { return health; }
    public final synchronized Set<FaultType> activeFaults() { return Set.copyOf(faults); }
    public final synchronized boolean hasFault(FaultType type) { return faults.contains(type); }
    public final synchronized void addFault(FaultType type) { faults.add(Objects.requireNonNull(type)); refreshHealth(); }
    public final synchronized void removeFault(FaultType type) { faults.remove(type); refreshHealth(); }
    public final synchronized void setHealth(HealthStatus value) { health = Objects.requireNonNull(value); }

    public final synchronized void update(Spacecraft spacecraft, SpaceEnvironment environment) {
        Objects.requireNonNull(spacecraft);
        Objects.requireNonNull(environment);
        applyEnvironment(spacecraft, environment);
        applyPhysics(spacecraft, environment);
        for (Sensor sensor : sensors) sensor.read();
        refreshHealth();
    }

    protected void applyEnvironment(Spacecraft spacecraft, SpaceEnvironment environment) {}
    protected abstract void applyPhysics(Spacecraft spacecraft, SpaceEnvironment environment);
    protected synchronized void refreshHealth() {
        if (!faults.isEmpty()) health = HealthStatus.WARNING;
        else health = SystemComponent.worstOf(sensors);
    }

    protected final synchronized void copyFaultsTo(Subsystem other) {
        other.faults.clear();
        other.faults.addAll(faults);
        other.health = health;
    }

    public abstract Subsystem copy(Spacecraft newOwner);
    @Override public final Subsystem copy() { throw new UnsupportedOperationException("Copy a subsystem with its owning spacecraft"); }
}

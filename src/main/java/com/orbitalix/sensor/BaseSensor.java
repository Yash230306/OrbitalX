package com.orbitalix.sensor;

import com.orbitalix.api.HealthStatus;
import com.orbitalix.util.RingBuffer;

import java.util.List;
import java.util.Objects;
import java.util.function.DoubleSupplier;

/** Abstract sensor template demonstrating inheritance, encapsulation, and bounded telemetry history. */
public abstract class BaseSensor implements Sensor {
    private final String id;
    private final String unit;
    private final DoubleSupplier source;
    private final RingBuffer<Reading> history;
    private volatile HealthStatus health = HealthStatus.NOMINAL;

    protected BaseSensor(String id, String unit, DoubleSupplier source) {
        this(id, unit, source, new RingBuffer<>(120));
    }

    protected BaseSensor(String id, String unit, DoubleSupplier source, RingBuffer<Reading> history) {
        if (id == null || id.isBlank() || unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Sensor id and unit are required");
        }
        this.id = id;
        this.unit = unit;
        this.source = Objects.requireNonNull(source);
        this.history = Objects.requireNonNull(history);
    }

    @Override public final String id() { return id; }
    @Override public final String name() { return id; }
    @Override public final String unit() { return unit; }
    @Override public final HealthStatus health() { return health; }
    public final void setHealth(HealthStatus value) { health = Objects.requireNonNull(value); }
    @Override public final List<com.orbitalix.model.SystemComponent> children() { return List.of(); }

    @Override public synchronized double measure() {
        double value = source.getAsDouble();
        if (!Double.isFinite(value)) throw new IllegalStateException("Non-finite telemetry from " + id);
        return value;
    }

    @Override public synchronized Reading read() {
        Reading reading = new Reading(measure(), System.currentTimeMillis());
        history.add(reading);
        return reading;
    }

    @Override public synchronized List<Reading> history() {
        return history.stream().toList();
    }

    protected final DoubleSupplier source() { return source; }
    protected abstract BaseSensor newCopy();

    public final synchronized void copyTelemetryTo(BaseSensor target) {
        Objects.requireNonNull(target);
        if (!id.equals(target.id)) throw new IllegalArgumentException("Sensor IDs must match when copying telemetry");
        synchronized (target) {
            target.history.clear();
            for (Reading reading : history) target.history.add(reading);
            target.health = health;
        }
    }

    @Override public synchronized BaseSensor copy() {
        BaseSensor result = newCopy();
        copyTelemetryTo(result);
        return result;
    }
}

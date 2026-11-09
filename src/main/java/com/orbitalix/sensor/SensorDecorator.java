package com.orbitalix.sensor;

import com.orbitalix.api.HealthStatus;
import com.orbitalix.model.SystemComponent;
import java.util.List;
import java.util.Objects;

/** Decorator base transparently wraps a sensor and alters its measurement strategy. */
public abstract class SensorDecorator implements Sensor {
    protected final Sensor delegate;
    protected SensorDecorator(Sensor delegate) { this.delegate = Objects.requireNonNull(delegate); }
    @Override public String id() { return delegate.id(); }
    @Override public String name() { return delegate.name(); }
    @Override public String unit() { return delegate.unit(); }
    @Override public HealthStatus health() { return delegate.health(); }
    @Override public List<SystemComponent> children() { return List.of(); }
    @Override public Reading read() {
        Reading result = new Reading(measure(), System.currentTimeMillis());
        return result;
    }
    @Override public List<Reading> history() { return delegate.history(); }
}

package com.orbitalix.sensor;

import java.util.function.DoubleSupplier;

/** Concrete temperature telemetry sensor. */
public final class TemperatureSensor extends BaseSensor {
    public TemperatureSensor(String id, DoubleSupplier source) { super(id, "C", source); }
    public TemperatureSensor(String id, double value) { this(id, () -> value); }
    @Override protected BaseSensor newCopy() { return new TemperatureSensor(id(), source()); }
}

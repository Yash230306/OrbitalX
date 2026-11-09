package com.orbitalix.sensor;

import java.util.function.DoubleSupplier;

public final class AtmosphereSensor extends BaseSensor {
    public AtmosphereSensor(String id, DoubleSupplier source) { super(id, "%", source); }
    public AtmosphereSensor(String id, double value) { this(id, () -> value); }
    @Override protected BaseSensor newCopy() { return new AtmosphereSensor(id(), source()); }
}

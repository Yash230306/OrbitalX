package com.orbitalix.sensor;

import java.util.function.DoubleSupplier;

public final class PressureSensor extends BaseSensor {
    public PressureSensor(String id, DoubleSupplier source) { super(id, "kPa", source); }
    public PressureSensor(String id, double value) { this(id, () -> value); }
    @Override protected BaseSensor newCopy() { return new PressureSensor(id(), source()); }
}

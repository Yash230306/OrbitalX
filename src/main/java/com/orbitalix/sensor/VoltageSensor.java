package com.orbitalix.sensor;

import java.util.function.DoubleSupplier;

public final class VoltageSensor extends BaseSensor {
    public VoltageSensor(String id, DoubleSupplier source) { super(id, "V", source); }
    public VoltageSensor(String id, double value) { this(id, () -> value); }
    @Override protected BaseSensor newCopy() { return new VoltageSensor(id(), source()); }
}

package com.orbitalix.sensor;

import java.util.function.DoubleSupplier;

public final class SignalSensor extends BaseSensor {
    public SignalSensor(String id, DoubleSupplier source) { super(id, "%", source); }
    public SignalSensor(String id, double value) { this(id, () -> value); }
    @Override protected BaseSensor newCopy() { return new SignalSensor(id(), source()); }
}

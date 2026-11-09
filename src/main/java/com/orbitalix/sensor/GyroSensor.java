package com.orbitalix.sensor;

import java.util.function.DoubleSupplier;

public final class GyroSensor extends BaseSensor {
    public GyroSensor(String id, DoubleSupplier source) { super(id, "deg/s", source); }
    public GyroSensor(String id, double value) { this(id, () -> value); }
    @Override protected BaseSensor newCopy() { return new GyroSensor(id(), source()); }
}

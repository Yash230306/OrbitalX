package com.orbitalix.sensor;

import java.util.function.DoubleSupplier;

public final class ComputerSensor extends BaseSensor {
    public ComputerSensor(String id, DoubleSupplier source) { super(id, "errors", source); }
    public ComputerSensor(String id, double value) { this(id, () -> value); }
    @Override protected BaseSensor newCopy() { return new ComputerSensor(id(), source()); }
}

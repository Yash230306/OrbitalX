package com.orbitalix.sensor;

import java.util.function.DoubleSupplier;

public final class RadiationSensor extends BaseSensor {
    public RadiationSensor(String id, DoubleSupplier source) { super(id, "mSv/h", source); }
    public RadiationSensor(String id, double value) { this(id, () -> value); }
    @Override protected BaseSensor newCopy() { return new RadiationSensor(id(), source()); }
}

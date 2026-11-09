package com.orbitalix.sensor;

import java.util.function.DoubleSupplier;

public final class CourseSensor extends BaseSensor {
    public CourseSensor(String id, DoubleSupplier source) { super(id, "deg", source); }
    public CourseSensor(String id, double value) { this(id, () -> value); }
    @Override protected BaseSensor newCopy() { return new CourseSensor(id(), source()); }
}

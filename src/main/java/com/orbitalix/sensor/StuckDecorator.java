package com.orbitalix.sensor;

public final class StuckDecorator extends SensorDecorator {
    private final double stuckValue;
    public StuckDecorator(Sensor delegate, double stuckValue) {
        super(delegate);
        if (!Double.isFinite(stuckValue)) throw new IllegalArgumentException("Stuck value must be finite");
        this.stuckValue = stuckValue;
    }
    @Override public double measure() { return stuckValue; }
    @Override public StuckDecorator copy() { return new StuckDecorator(delegate.copy(), stuckValue); }
}

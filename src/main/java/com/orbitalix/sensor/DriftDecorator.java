package com.orbitalix.sensor;

public final class DriftDecorator extends SensorDecorator {
    private final double driftPerRead;
    private double accumulated;
    public DriftDecorator(Sensor delegate, double driftPerRead) {
        super(delegate);
        if (!Double.isFinite(driftPerRead)) throw new IllegalArgumentException("Drift must be finite");
        this.driftPerRead = driftPerRead;
    }
    @Override public synchronized double measure() {
        accumulated += driftPerRead;
        return delegate.measure() + accumulated;
    }
    @Override public synchronized DriftDecorator copy() {
        DriftDecorator result = new DriftDecorator(delegate.copy(), driftPerRead);
        result.accumulated = accumulated;
        return result;
    }
}

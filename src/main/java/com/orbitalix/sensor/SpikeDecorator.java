package com.orbitalix.sensor;

public final class SpikeDecorator extends SensorDecorator {
    private final double spike;
    private boolean armed = true;
    public SpikeDecorator(Sensor delegate, double spike) {
        super(delegate);
        if (!Double.isFinite(spike)) throw new IllegalArgumentException("Spike must be finite");
        this.spike = spike;
    }
    @Override public synchronized double measure() {
        if (!armed) return delegate.measure();
        armed = false;
        return delegate.measure() + spike;
    }
    @Override public synchronized SpikeDecorator copy() {
        SpikeDecorator result = new SpikeDecorator(delegate.copy(), spike);
        result.armed = armed;
        return result;
    }
}

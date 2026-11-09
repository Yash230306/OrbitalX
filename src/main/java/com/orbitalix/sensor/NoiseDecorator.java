package com.orbitalix.sensor;

import com.orbitalix.util.DeterministicRandom;

public final class NoiseDecorator extends SensorDecorator {
    private final double amplitude;
    private final DeterministicRandom random;
    public NoiseDecorator(Sensor delegate, double amplitude, long seed) {
        this(delegate, amplitude, new DeterministicRandom(seed));
    }
    private NoiseDecorator(Sensor delegate, double amplitude, DeterministicRandom random) {
        super(delegate);
        if (!Double.isFinite(amplitude) || amplitude < 0) throw new IllegalArgumentException("Noise amplitude must be non-negative");
        this.amplitude = amplitude;
        this.random = random;
    }
    @Override public double measure() { return delegate.measure() + (random.nextDouble() * 2 - 1) * amplitude; }
    @Override public NoiseDecorator copy() { return new NoiseDecorator(delegate.copy(), amplitude, random.copy()); }
}

package com.orbitalix.detection;

import com.orbitalix.api.Severity;
import com.orbitalix.sensor.*;
import java.util.*;

/** Detects an unexpectedly large single-sample telemetry change. */
public final class RateOfChangeDetector implements AnomalyDetector {
    private final double minimumChange;
    public RateOfChangeDetector() { this(2.0); }
    public RateOfChangeDetector(double minimumChange) {
        if (!Double.isFinite(minimumChange) || minimumChange <= 0) throw new IllegalArgumentException("Minimum change must be positive");
        this.minimumChange = minimumChange;
    }
    @Override public String name() { return "Rate of Change"; }
    @Override public Optional<Anomaly> detect(Sensor sensor, int tick) {
        List<Reading> readings = sensor.history();
        if (readings.size() < 2) return Optional.empty();
        double previous = readings.get(readings.size() - 2).value();
        double current = readings.get(readings.size() - 1).value();
        double change = Math.abs(current - previous);
        if (change < minimumChange) return Optional.empty();
        return Optional.of(new Anomaly(sensor, name(), change >= minimumChange * 2 ? Severity.CRITICAL : Severity.MEDIUM,
                current, previous, tick, sensor.id() + " changed by " + change));
    }
}

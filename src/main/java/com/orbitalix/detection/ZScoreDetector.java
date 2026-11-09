package com.orbitalix.detection;

import com.orbitalix.api.Severity;
import com.orbitalix.sensor.*;
import java.util.*;

/** Detects statistically unusual readings relative to the sensor's recent history. */
public final class ZScoreDetector implements AnomalyDetector {
    private final int minimumSamples;
    private final double threshold;
    public ZScoreDetector() { this(8, 3); }
    public ZScoreDetector(int minimumSamples, double threshold) {
        if (minimumSamples < 3 || !Double.isFinite(threshold) || threshold <= 0)
            throw new IllegalArgumentException("Invalid z-score configuration");
        this.minimumSamples = minimumSamples;
        this.threshold = threshold;
    }
    @Override public String name() { return "Z-Score"; }
    @Override public Optional<Anomaly> detect(Sensor sensor, int tick) {
        List<Reading> readings = sensor.history();
        if (readings.size() < minimumSamples) return Optional.empty();
        int end = readings.size() - 1;
        double mean = readings.subList(0, end).stream().mapToDouble(Reading::value).average().orElseThrow();
        double variance = readings.subList(0, end).stream().mapToDouble(r -> Math.pow(r.value() - mean, 2)).average().orElse(0);
        double deviation = Math.sqrt(variance);
        if (deviation == 0) return Optional.empty();
        double value = readings.get(end).value();
        double z = Math.abs(value - mean) / deviation;
        if (z < threshold) return Optional.empty();
        return Optional.of(new Anomaly(sensor, name(), z >= threshold * 2 ? Severity.CRITICAL : Severity.HIGH,
                value, mean, tick, sensor.id() + " has z-score " + String.format(Locale.ROOT, "%.2f", z)));
    }
}

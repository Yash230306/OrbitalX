package com.orbitalix.detection;

import com.orbitalix.api.Severity;
import com.orbitalix.sensor.*;
import java.util.*;

/** Linear least-squares forecast over the sensor's bounded recent sample window. */
public final class TrendPredictor {
    public record Prediction(String sensorId, double current, double target, double changePerTick,
                            int ticksToThreshold, Severity severity) {}
    private final int windowSize;
    public TrendPredictor() { this(12); }
    public TrendPredictor(int windowSize) {
        if (windowSize < 2) throw new IllegalArgumentException("Trend window must contain at least two samples");
        this.windowSize = windowSize;
    }
    public Optional<Prediction> predict(Sensor sensor, double target, boolean increasing, int tick) {
        if (!Double.isFinite(target) || tick < 0) throw new IllegalArgumentException("Invalid prediction target or tick");
        List<Reading> readings = sensor.history();
        if (readings.size() < 2) return Optional.empty();
        int start = Math.max(0, readings.size() - windowSize);
        List<Reading> samples = readings.subList(start, readings.size());
        double meanX = (samples.size() - 1) / 2.0;
        double meanY = samples.stream().mapToDouble(Reading::value).average().orElseThrow();
        double numerator = 0;
        double denominator = 0;
        for (int i = 0; i < samples.size(); i++) {
            numerator += (i - meanX) * (samples.get(i).value() - meanY);
            denominator += (i - meanX) * (i - meanX);
        }
        double slope = numerator / denominator;
        if (increasing ? slope <= 0 : slope >= 0) return Optional.empty();
        double current = samples.get(samples.size() - 1).value();
        double ticks = (target - current) / slope;
        if (!Double.isFinite(ticks) || ticks < 0) return Optional.empty();
        int estimate = (int) Math.min(Integer.MAX_VALUE, Math.ceil(ticks));
        return Optional.of(new Prediction(sensor.id(), current, target, slope, estimate,
                estimate <= 5 ? Severity.CRITICAL : estimate <= 20 ? Severity.HIGH : Severity.MEDIUM));
    }
}

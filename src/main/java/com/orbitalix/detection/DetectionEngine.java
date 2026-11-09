package com.orbitalix.detection;

import com.orbitalix.sensor.Sensor;
import java.util.*;

/** Runs all configured detectors against one freshly sampled sensor value per tick. */
public final class DetectionEngine {
    private final List<AnomalyDetector> detectors;
    public DetectionEngine() {
        this(List.of(new ThresholdDetector(), new RateOfChangeDetector(), new ZScoreDetector()));
    }
    public DetectionEngine(List<? extends AnomalyDetector> detectors) {
        if (detectors == null || detectors.isEmpty()) throw new IllegalArgumentException("At least one detector is required");
        this.detectors = List.copyOf(detectors);
    }
    public List<AnomalyDetector> detectors() { return detectors; }
    public List<Anomaly> scan(Collection<? extends Sensor> sensors, int tick) {
        if (sensors == null || tick < 0) throw new IllegalArgumentException("Sensors and a non-negative tick are required");
        List<Anomaly> anomalies = new ArrayList<>();
        for (Sensor sensor : sensors) {
            sensor.read();
            for (AnomalyDetector detector : detectors) detector.detect(sensor, tick).ifPresent(anomalies::add);
        }
        return List.copyOf(anomalies);
    }
}

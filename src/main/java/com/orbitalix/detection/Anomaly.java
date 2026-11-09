package com.orbitalix.detection;

import com.orbitalix.api.Severity;
import com.orbitalix.sensor.Sensor;
import java.util.Objects;

/** Immutable finding tied to the sensor that produced the anomalous telemetry. */
public record Anomaly(Sensor sensor, String detector, Severity severity, double value,
                      double threshold, int tick, String description) {
    public Anomaly {
        Objects.requireNonNull(sensor);
        Objects.requireNonNull(detector);
        Objects.requireNonNull(severity);
        Objects.requireNonNull(description);
        if (!Double.isFinite(value) || !Double.isFinite(threshold) || tick < 0)
            throw new IllegalArgumentException("Invalid anomaly measurement");
    }
    public String id() { return sensor.id() + ":" + detector; }
}

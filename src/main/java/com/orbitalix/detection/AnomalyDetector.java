package com.orbitalix.detection;

import com.orbitalix.sensor.Sensor;
import java.util.Optional;

public interface AnomalyDetector {
    String name();
    Optional<Anomaly> detect(Sensor sensor, int tick);
}

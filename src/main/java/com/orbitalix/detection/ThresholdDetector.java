package com.orbitalix.detection;

import com.orbitalix.api.Severity;
import com.orbitalix.sensor.Sensor;
import java.util.*;

/** Detects values outside sensor-specific warning and critical limits. */
public final class ThresholdDetector implements AnomalyDetector {
    public enum Direction { ABOVE, BELOW }
    public record Threshold(Direction direction, double warning, double critical) {
        public Threshold {
            Objects.requireNonNull(direction);
            if (!Double.isFinite(warning) || !Double.isFinite(critical)
                    || (direction == Direction.ABOVE && critical < warning)
                    || (direction == Direction.BELOW && critical > warning))
                throw new IllegalArgumentException("Critical limit must be beyond warning limit");
        }
    }

    private final Map<String, Threshold> thresholds;
    public ThresholdDetector() {
        Map<String, Threshold> defaults = new LinkedHashMap<>();
        put(defaults, Direction.BELOW, 20, 5, "battery", "bus-voltage");
        put(defaults, Direction.BELOW, 25, 10, "oxygen");
        put(defaults, Direction.ABOVE, 85, 100, "temperature", "engine-temperature", "cabin-temperature");
        put(defaults, Direction.BELOW, 80, 60, "pressure", "cabin-pressure");
        put(defaults, Direction.ABOVE, 4, 6, "co2", "cabin-co2", "atmosphere");
        put(defaults, Direction.ABOVE, 3, 6, "radiation");
        put(defaults, Direction.ABOVE, 5, 10, "course", "gyro");
        put(defaults, Direction.BELOW, .25, .1, "signal", "uplink", "uplink-signal");
        put(defaults, Direction.ABOVE, 10, 15, "computer-errors");
        thresholds = Map.copyOf(defaults);
    }
    public ThresholdDetector(Map<String, Threshold> thresholds) {
        if (thresholds == null || thresholds.isEmpty()) throw new IllegalArgumentException("Thresholds are required");
        this.thresholds = Map.copyOf(thresholds);
    }
    private static void put(Map<String, Threshold> map, Direction direction, double warning, double critical, String... ids) {
        Threshold limit = new Threshold(direction, warning, critical);
        for (String id : ids) map.put(id.toLowerCase(Locale.ROOT), limit);
    }
    @Override public String name() { return "Threshold"; }

    @Override public Optional<Anomaly> detect(Sensor sensor, int tick) {
        Threshold limits = thresholds.get(sensor.id().toLowerCase(Locale.ROOT));
        if (limits == null) return Optional.empty();
        double value = sensor.latest();
        boolean critical = crossed(value, limits.critical(), limits.direction());
        boolean warning = crossed(value, limits.warning(), limits.direction());
        if (!warning) return Optional.empty();
        double boundary = critical ? limits.critical() : limits.warning();
        return Optional.of(new Anomaly(sensor, name(), critical ? Severity.CRITICAL : Severity.HIGH,
                value, boundary, tick, sensor.id() + " crossed " + boundary));
    }
    private static boolean crossed(double value, double boundary, Direction direction) {
        return direction == Direction.ABOVE ? value >= boundary : value <= boundary;
    }
}

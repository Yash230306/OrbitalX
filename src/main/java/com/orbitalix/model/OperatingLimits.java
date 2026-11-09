package com.orbitalix.model;

import com.orbitalix.api.LimitType;
import com.orbitalix.exception.LimitOverrideException;
import java.util.EnumMap;
import java.util.Map;

/** Operating constraints distinguish warning thresholds from immutable engineering maxima. */
public final class OperatingLimits {
    private final EnumMap<LimitType, Double> values = new EnumMap<>(LimitType.class);
    private final EnumMap<LimitType, Double> design = new EnumMap<>(LimitType.class);
    private boolean overridden;

    public OperatingLimits() {
        values.put(LimitType.MAX_THROTTLE, 80d);
        values.put(LimitType.MAX_TEMPERATURE, 85d);
        values.put(LimitType.MIN_BATTERY, 20d);
        values.put(LimitType.MIN_FUEL_RESERVE, 15d);
        design.put(LimitType.MAX_THROTTLE, 100d);
        design.put(LimitType.MAX_TEMPERATURE, 100d);
        design.put(LimitType.MIN_BATTERY, 100d);
        design.put(LimitType.MIN_FUEL_RESERVE, 100d);
    }

    private OperatingLimits(OperatingLimits source) {
        values.putAll(source.values);
        design.putAll(source.design);
        overridden = source.overridden;
    }

    public synchronized double get(LimitType type) { return require(type, values); }
    public synchronized double designValue(LimitType type) { return require(type, design); }
    public synchronized boolean overridden() { return overridden; }
    public synchronized Map<LimitType, Double> values() { return Map.copyOf(values); }

    public synchronized void set(LimitType type, double value, boolean override) throws LimitOverrideException {
        require(type, values);
        if (!Double.isFinite(value) || value < 0) throw new IllegalArgumentException("Limit must be finite and non-negative");
        if (value > design.get(type)) {
            if (!override) throw new LimitOverrideException("Override required for " + type + " above design value");
            overridden = true;
        }
        values.put(type, value);
    }

    public synchronized void restoreSafeDefaults() {
        values.put(LimitType.MAX_THROTTLE, 80d);
        values.put(LimitType.MAX_TEMPERATURE, 85d);
        values.put(LimitType.MIN_BATTERY, 20d);
        values.put(LimitType.MIN_FUEL_RESERVE, 15d);
        overridden = false;
    }

    public synchronized OperatingLimits copy() { return new OperatingLimits(this); }

    private static double require(LimitType type, EnumMap<LimitType, Double> map) {
        Double value = map.get(type);
        if (value == null) throw new IllegalArgumentException("Unsupported limit: " + type);
        return value;
    }
}

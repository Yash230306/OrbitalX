package com.orbitalix.sensor;

public record Reading(double value, long timestamp) {
    public Reading {
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Sensor value must be finite");
        if (timestamp < 0) throw new IllegalArgumentException("Timestamp cannot be negative");
    }
}

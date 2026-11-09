package com.orbitalix.util;

public final class TickFormatter {
    private TickFormatter() {}

    public static String format(int tick) {
        if (tick < 0) throw new IllegalArgumentException("Tick cannot be negative");
        return "T+" + String.format("%02d:%02d:%02d", tick / 7200, (tick / 120) % 60, (tick / 2) % 60);
    }
}

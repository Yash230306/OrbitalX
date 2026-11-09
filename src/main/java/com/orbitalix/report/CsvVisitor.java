package com.orbitalix.report;

import com.orbitalix.api.*;
import java.util.Locale;

public final class CsvVisitor implements ReportVisitor<String> {
    @Override public String visit(MissionReport report) {
        StringBuilder csv = new StringBuilder("tick,fuel,battery,oxygen,temperature,pressure,progress,status\r\n");
        for (SimulationSnapshot snapshot : report.history()) {
            csv.append(snapshot.tick()).append(',')
                    .append(value(snapshot, "Fuel")).append(',')
                    .append(value(snapshot, "Battery")).append(',')
                    .append(value(snapshot, "Oxygen")).append(',')
                    .append(value(snapshot, "Thermal")).append(',')
                    .append(value(snapshot, "Life Support")).append(',')
                    .append(String.format(Locale.ROOT, "%.3f", snapshot.mission().progressPercent())).append(',')
                    .append(snapshot.spacecraftState()).append("\r\n");
        }
        return csv.toString();
    }
    private static double value(SimulationSnapshot snapshot, String resourceName) {
        return snapshot.resources().stream().filter(resource -> resource.name().equals(resourceName))
                .mapToDouble(ResourceSnapshot::level).findFirst().orElseGet(() ->
                        snapshot.subsystems().stream().filter(subsystem -> subsystem.name().equals(resourceName))
                                .flatMap(subsystem -> subsystem.sensors().stream()).mapToDouble(SensorSnapshot::value)
                                .findFirst().orElse(0));
    }
}

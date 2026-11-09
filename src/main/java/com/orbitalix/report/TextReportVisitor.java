package com.orbitalix.report;

import com.orbitalix.api.SimulationSnapshot;
import java.util.stream.Collectors;

public final class TextReportVisitor implements ReportVisitor<String> {
    @Override public String visit(MissionReport report) {
        SimulationSnapshot snapshot = report.current();
        String events = report.events().stream().map(event -> "T+" + event.tick() + " [" + event.severity() + "] " + event.message())
                .collect(Collectors.joining(System.lineSeparator()));
        return "OrbitalX Mission Report" + System.lineSeparator() + snapshot.mission().outcome()
                + System.lineSeparator() + "Ticks: " + snapshot.tick() + " | Grade: " + snapshot.score().grade()
                + System.lineSeparator() + events;
    }
}

package com.orbitalix.report;

import com.orbitalix.api.*;
import java.util.List;
import java.util.Objects;

public record MissionReport(SimulationSnapshot current, List<SimulationSnapshot> history,
                            List<MissionEvent> events) implements Visitable {
    public MissionReport {
        Objects.requireNonNull(current);
        history = List.copyOf(history);
        events = List.copyOf(events);
    }
    @Override public <T> T accept(ReportVisitor<T> visitor) { return visitor.visit(this); }
}

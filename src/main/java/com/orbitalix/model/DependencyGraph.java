package com.orbitalix.model;

import com.orbitalix.api.DependencySnapshot;
import com.orbitalix.api.HealthStatus;
import java.util.List;

/** Dependency graph exposes cascade edges and source-health propagation for SystemsView. */
public final class DependencyGraph {
    public record Edge(String from, String to) {
        public Edge {
            if (from == null || from.isBlank() || to == null || to.isBlank()) throw new IllegalArgumentException("Edge labels required");
        }
    }
    private final List<Edge> edges = List.of(new Edge("Power", "Computer"), new Edge("Thermal", "Propulsion"),
            new Edge("Life Support", "Crew"), new Edge("Attitude", "Navigation"), new Edge("Communication", "Uplink"));
    public List<Edge> edges() { return edges; }
    public List<DependencySnapshot> snapshot(Spacecraft craft) {
        return edges.stream().map(edge -> {
            HealthStatus status = edge.from().equals("Crew")
                    ? craft.crewRoster().members().stream().map(member -> member.health())
                            .max(java.util.Comparator.comparingInt(Enum::ordinal)).orElse(HealthStatus.NOMINAL)
                    : craft.subsystems().stream().filter(subsystem -> subsystem.name().equals(edge.from()))
                            .map(Subsystem::health).findFirst().orElse(HealthStatus.NOMINAL);
            return new DependencySnapshot(edge.from(), edge.to(), status,
                    status.ordinal() >= HealthStatus.CRITICAL.ordinal());
        }).toList();
    }
}

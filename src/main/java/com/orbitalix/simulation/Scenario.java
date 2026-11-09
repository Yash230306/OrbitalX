package com.orbitalix.simulation;

import com.orbitalix.api.FaultType;
import java.util.List;

/** Immutable scripted sequence for one deterministic mission scenario. */
public record Scenario(int index, String name, List<Trigger> triggers) {
    public Scenario {
        if (index < 0 || name == null || name.isBlank()) throw new IllegalArgumentException("Invalid scenario");
        triggers = List.copyOf(triggers);
    }
    public record Trigger(int tick, String target, FaultType fault) {
        public Trigger {
            if (tick < 1 || target == null || target.isBlank() || fault == null)
                throw new IllegalArgumentException("Invalid scenario trigger");
        }
    }
    public List<Trigger> triggersAt(int tick) { return triggers.stream().filter(trigger -> trigger.tick() == tick).toList(); }
}

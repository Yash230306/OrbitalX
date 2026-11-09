package com.orbitalix.crew;

import com.orbitalix.api.CrewTaskType;
import com.orbitalix.api.FaultType;
import com.orbitalix.model.Spacecraft;
import java.util.Objects;

/** Command object advances a crew assignment and applies its effect on completion. */
public final class CrewTask {
    private final CrewTaskType type;
    private final FaultType targetFault;
    private final int duration;
    private int remaining;
    private boolean complete;

    public CrewTask(CrewTaskType type, FaultType targetFault, int duration) {
        this.type = Objects.requireNonNull(type);
        if (duration < 1) throw new IllegalArgumentException("Task duration must be positive");
        if (type == CrewTaskType.EVA_REPAIR && targetFault == null)
            throw new IllegalArgumentException("EVA repair requires a fault target");
        this.targetFault = targetFault;
        this.duration = duration;
        this.remaining = duration;
    }

    public CrewTaskType type() { return type; }
    public FaultType targetFault() { return targetFault; }
    public int duration() { return duration; }
    public synchronized int remaining() { return remaining; }
    public synchronized boolean complete() { return complete; }

    public synchronized boolean tick(Spacecraft craft) {
        if (complete) return true;
        if (--remaining > 0) return false;
        switch (type) {
            case EVA_REPAIR -> craft.clearFault(targetFault);
            case MEDICAL_CARE -> craft.crewRoster().members().stream()
                    .filter(member -> member.healthPercent() < 100).findFirst().ifPresent(member -> member.heal(30));
            case MANUAL_REBOOT -> craft.restartComputer();
            case REST -> { }
        }
        complete = true;
        return true;
    }

    public CrewTask copy() {
        CrewTask result = new CrewTask(type, targetFault, duration);
        result.remaining = remaining;
        result.complete = complete;
        return result;
    }
}

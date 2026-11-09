package com.orbitalix.replay;

import com.orbitalix.api.SimulationSnapshot;
import java.util.Optional;

public final class ReplayController {
    public Optional<SimulationSnapshot> at(History<SimulationSnapshot> history, int tick) {
        return history.stream().filter(snapshot -> snapshot.tick() == tick).findFirst();
    }
    public SimulationSnapshot require(History<SimulationSnapshot> history, int tick) {
        return at(history, tick).orElseThrow(() -> new IllegalArgumentException("No recorded tick " + tick));
    }
    public SimulationSnapshot asReplay(SimulationSnapshot item) {
        return new SimulationSnapshot(item.tick(), true, item.spacecraftState(), item.throttle(), item.autonomyMode(),
                item.autopilotOnline(), item.selfHealing(), item.config(), item.mission(), item.subsystems(),
                item.resources(), item.limits(), item.recommendations(), item.threats(), item.orbit(), item.crew(),
                item.score(), item.comms(), item.dependencies());
    }
}

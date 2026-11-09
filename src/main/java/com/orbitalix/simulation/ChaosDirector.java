package com.orbitalix.simulation;

import com.orbitalix.api.FaultType;
import com.orbitalix.config.Difficulty;
import com.orbitalix.util.DeterministicRandom;
import java.util.Optional;

/** Seeded difficulty-scaled event generator used only by the Endless Chaos scenario. */
public final class ChaosDirector {
    private static final FaultType[] EVENTS = {FaultType.OVERHEAT, FaultType.FUEL_LEAK,
            FaultType.SOLAR_FAILURE, FaultType.NAV_DRIFT};
    public Optional<FaultType> faultAt(int tick, Difficulty difficulty, DeterministicRandom random) {
        if (tick < 1 || difficulty == null || random == null) throw new IllegalArgumentException("Invalid chaos input");
        int interval = Math.max(12, (int) Math.round(40 / difficulty.faultMultiplier()));
        if (tick % interval != 0) return Optional.empty();
        return Optional.of(EVENTS[random.nextInt(EVENTS.length)]);
    }
}

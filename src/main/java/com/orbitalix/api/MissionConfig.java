package com.orbitalix.api;

import com.orbitalix.config.CraftType;
import com.orbitalix.config.Destination;
import com.orbitalix.config.Difficulty;
import java.util.Objects;

public record MissionConfig(Destination destination, CraftType craftType, Difficulty difficulty, long seed,
                            boolean chaosDirector, int scenarioIndex) {
    public MissionConfig {
        if (destination == null || craftType == null || difficulty == null) {
            throw new IllegalArgumentException("Mission options cannot be null");
        }
        if (scenarioIndex < 0) throw new IllegalArgumentException("Scenario index cannot be negative");
    }

    public static MissionConfig defaults() {
        return new MissionConfig(Destination.MARS_TRANSFER, CraftType.CREW_CAPSULE,
                Difficulty.NORMAL, 42L, false, 0);
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Destination destination = Destination.MARS_TRANSFER;
        private CraftType craftType = CraftType.CREW_CAPSULE;
        private Difficulty difficulty = Difficulty.NORMAL;
        private long seed = 42L;
        private boolean chaosDirector;
        private int scenarioIndex;

        private Builder() {}
        public Builder destination(Destination value) { destination = Objects.requireNonNull(value); return this; }
        public Builder craftType(CraftType value) { craftType = Objects.requireNonNull(value); return this; }
        public Builder difficulty(Difficulty value) { difficulty = Objects.requireNonNull(value); return this; }
        public Builder seed(long value) { seed = value; return this; }
        public Builder chaosDirector(boolean value) { chaosDirector = value; return this; }
        public Builder scenarioIndex(int value) { scenarioIndex = value; return this; }
        public MissionConfig build() {
            return new MissionConfig(destination, craftType, difficulty, seed, chaosDirector, scenarioIndex);
        }
    }
}

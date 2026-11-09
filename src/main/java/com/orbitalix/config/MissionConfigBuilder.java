package com.orbitalix.config;

import com.orbitalix.api.MissionConfig;

/** Builder pattern keeps optional mission settings explicit and validates at construction. */
public final class MissionConfigBuilder {
    private final MissionConfig.Builder builder = MissionConfig.builder();

    public MissionConfigBuilder destination(Destination value) { builder.destination(value); return this; }
    public MissionConfigBuilder craftType(CraftType value) { builder.craftType(value); return this; }
    public MissionConfigBuilder difficulty(Difficulty value) { builder.difficulty(value); return this; }
    public MissionConfigBuilder seed(long value) { builder.seed(value); return this; }
    public MissionConfigBuilder chaosDirector(boolean value) { builder.chaosDirector(value); return this; }
    public MissionConfigBuilder scenarioIndex(int value) { builder.scenarioIndex(value); return this; }
    public MissionConfig build() { return builder.build(); }
}

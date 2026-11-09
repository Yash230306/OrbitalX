package com.orbitalix.state;

import com.orbitalix.api.HealthStatus;

public final class SafeModeState implements SpacecraftState {
    @Override public String name() { return "SAFE_MODE"; }
    @Override public SpacecraftState transition(HealthStatus health) {
        return health.ordinal() >= HealthStatus.CRITICAL.ordinal() ? this : StateTransitions.from(health);
    }
    @Override public SafeModeState copy() { return new SafeModeState(); }
}

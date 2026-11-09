package com.orbitalix.state;

import com.orbitalix.api.HealthStatus;

public final class WarningState implements SpacecraftState {
    @Override public String name() { return "WARNING"; }
    @Override public SpacecraftState transition(HealthStatus health) {
        return health == HealthStatus.WARNING ? this : StateTransitions.from(health);
    }
    @Override public WarningState copy() { return new WarningState(); }
}

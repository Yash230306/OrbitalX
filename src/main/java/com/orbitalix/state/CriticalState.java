package com.orbitalix.state;

import com.orbitalix.api.HealthStatus;

public final class CriticalState implements SpacecraftState {
    @Override public String name() { return "CRITICAL"; }
    @Override public SpacecraftState transition(HealthStatus health) {
        return health == HealthStatus.CRITICAL ? this : StateTransitions.from(health);
    }
    @Override public CriticalState copy() { return new CriticalState(); }
}

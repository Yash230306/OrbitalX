package com.orbitalix.state;

import com.orbitalix.api.HealthStatus;

public final class NominalState implements SpacecraftState {
    @Override public String name() { return "NOMINAL"; }
    @Override public SpacecraftState transition(HealthStatus health) {
        return health == HealthStatus.NOMINAL ? this : StateTransitions.from(health);
    }
    @Override public NominalState copy() { return new NominalState(); }
}

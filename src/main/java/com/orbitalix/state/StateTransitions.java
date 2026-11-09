package com.orbitalix.state;

import com.orbitalix.api.HealthStatus;

final class StateTransitions {
    private StateTransitions() {}
    static SpacecraftState from(HealthStatus health) {
        return switch (health) {
            case NOMINAL -> new NominalState();
            case WARNING -> new WarningState();
            case CRITICAL, FAILED -> new CriticalState();
        };
    }
}

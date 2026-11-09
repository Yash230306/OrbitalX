package com.orbitalix.autonomy;

import com.orbitalix.advisor.Recommendation;
import com.orbitalix.event.EventBus;
import com.orbitalix.model.Spacecraft;
import java.util.Objects;

public record ResponseContext(Spacecraft spacecraft, Recommendation recommendation, AutonomyPolicy policy,
                              boolean selfHealing, boolean computerOnline, boolean evaAllowed,
                              int waitingTicks, int responseWindow, int tick, int failedAttempts,
                              boolean escalated, EventBus eventBus) {
    public ResponseContext {
        Objects.requireNonNull(spacecraft);
        Objects.requireNonNull(recommendation);
        Objects.requireNonNull(policy);
        Objects.requireNonNull(eventBus);
        if (waitingTicks < 0 || responseWindow < 1 || tick < 0 || failedAttempts < 0)
            throw new IllegalArgumentException("Invalid response timing");
    }
    public ResponseContext withFailureCount(int count) {
        return new ResponseContext(spacecraft, recommendation, policy, selfHealing, computerOnline, evaAllowed,
                waitingTicks, responseWindow, tick, count, escalated, eventBus);
    }
    public ResponseContext markEscalated() {
        return new ResponseContext(spacecraft, recommendation, policy, selfHealing, computerOnline, evaAllowed,
                waitingTicks, responseWindow, tick, failedAttempts, true, eventBus);
    }
}

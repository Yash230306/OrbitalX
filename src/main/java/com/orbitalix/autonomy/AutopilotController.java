package com.orbitalix.autonomy;

import com.orbitalix.advisor.Recommendation;
import com.orbitalix.api.AutonomyMode;
import com.orbitalix.event.EventBus;
import com.orbitalix.model.Spacecraft;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** Coordinates policy selection and the Self-healing -> Autopilot -> Crew -> Escalation chain. */
public final class AutopilotController {
    private final EventBus eventBus;
    private final SelfHealingHandler selfHealing = new SelfHealingHandler();
    private final ResponseHandler first;
    private final Map<String, Integer> waitTicks = new ConcurrentHashMap<>();
    private final int responseWindow;
    public AutopilotController(EventBus eventBus, int responseWindow) {
        if (responseWindow < 1) throw new IllegalArgumentException("Response window must be positive");
        this.eventBus = Objects.requireNonNull(eventBus);
        this.responseWindow = responseWindow;
        ResponseHandler autopilot = new AutopilotHandler();
        ResponseHandler crew = new CrewHandler();
        autopilot.setNext(crew).setNext(new EscalationHandler());
        selfHealing.setNext(autopilot);
        first = selfHealing;
    }
    public ResponseDecision respond(Spacecraft craft, Recommendation recommendation, AutonomyMode mode,
                                    boolean selfHealingEnabled, int tick) {
        int waiting = waitTicks.getOrDefault(recommendation.id(), 0);
        AutonomyPolicy policy = switch (mode) {
            case MANUAL -> new ManualPolicy();
            case TIMED_AUTOPILOT -> new TimedAutopilotPolicy();
            case FULL_AUTO -> new FullAutoPolicy();
        };
        boolean evaAllowed = craft.radiation() < 3 && craft.pressure() >= 80 && craft.oxygen() >= 25;
        ResponseContext context = new ResponseContext(craft, recommendation, policy, selfHealingEnabled,
                !craft.computerOffline(), evaAllowed, waiting, responseWindow, tick, 0, false, eventBus);
        ResponseDecision decision = first.handle(context);
        if (decision.handled() && decision.result() != null && decision.result().successful())
            waitTicks.remove(recommendation.id());
        else waitTicks.merge(recommendation.id(), 1, Integer::sum);
        return decision;
    }
    public int waitingTicks(String recommendationId) { return waitTicks.getOrDefault(recommendationId, 0); }
}

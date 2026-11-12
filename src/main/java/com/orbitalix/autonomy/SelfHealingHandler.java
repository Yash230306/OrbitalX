package com.orbitalix.autonomy;

import com.orbitalix.action.ActionResult;
import com.orbitalix.api.ActionRisk;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class SelfHealingHandler extends ResponseHandler {
    private final ConcurrentMap<String, Integer> failures = new ConcurrentHashMap<>();
    @Override protected ResponseDecision tryHandle(ResponseContext context) {
        if (!context.computerOnline() || !context.selfHealing()
                || context.recommendation().risk() != ActionRisk.SAFE) return null;
        int failuresBefore = Math.max(context.failedAttempts(), failures.getOrDefault(context.recommendation().id(), 0));
        if (failuresBefore >= 2) return nextEscalated(context.withFailureCount(failuresBefore).markEscalated());
        ActionResult result = context.recommendation().action().execute(context.spacecraft());
        if (result.successful()) {
            failures.remove(context.recommendation().id());
            return new ResponseDecision(true, "SELF_HEALING", result, false, false);
        }
        int attempts = failures.merge(context.recommendation().id(), 1, Integer::sum);
        if (attempts >= 2) return nextEscalated(context.withFailureCount(attempts).markEscalated());
        // Not yet escalated but this attempt failed — return an unsuccessful decision so the
        // recommendation is not removed and the next tick gets another chance.
        return new ResponseDecision(false, "SELF_HEALING", result, false, false);
    }
    private ResponseDecision nextEscalated(ResponseContext context) {
        return pass(context);
    }
}

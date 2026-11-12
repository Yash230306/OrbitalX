package com.orbitalix.autonomy;

import com.orbitalix.action.ActionResult;
import com.orbitalix.api.ActionRisk;

/**
 * Chain-of-Responsibility handler that executes corrective actions under autopilot authority.
 *
 * <p><b>OOP fix (Chain-of-Responsibility completeness):</b> the previous implementation only
 * handled {@link ActionRisk#COSTLY} recommendations.  When self-healing was disabled, any
 * {@link ActionRisk#SAFE} recommendation that the {@code SelfHealingHandler} skipped would
 * fall through the entire chain unhandled and trigger a spurious escalation — even in
 * {@code FULL_AUTO} mode where the system is authorised to act on everything.</p>
 *
 * <p>The fix: the autopilot now also executes {@code SAFE} recommendations when self-healing
 * is disabled.  When self-healing is enabled, {@code SelfHealingHandler} has already claimed
 * {@code SAFE} recommendations before this handler is reached, so there is no duplication.</p>
 */
public final class AutopilotHandler extends ResponseHandler {

    @Override protected ResponseDecision tryHandle(ResponseContext context) {
        if (!context.computerOnline()) return null;
        if (!context.policy().shouldAct(context.recommendation(), context.waitingTicks(), context.responseWindow()))
            return null;

        ActionRisk risk = context.recommendation().risk();

        // Handle COSTLY actions unconditionally (requires autopilot authority).
        // Handle SAFE actions only when self-healing is disabled; otherwise
        // SelfHealingHandler would already have executed them further up the chain.
        boolean shouldHandle = (risk == ActionRisk.COSTLY)
                || (risk == ActionRisk.SAFE && !context.selfHealing());

        if (!shouldHandle) return null;

        ActionResult result = context.recommendation().action().execute(context.spacecraft());
        if (!result.successful()) return null;
        return new ResponseDecision(true, "AUTOPILOT", result, false, context.escalated());
    }
}


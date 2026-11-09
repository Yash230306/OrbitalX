package com.orbitalix.autonomy;

import com.orbitalix.action.ActionResult;
import com.orbitalix.api.ActionRisk;

public final class AutopilotHandler extends ResponseHandler {
    @Override protected ResponseDecision tryHandle(ResponseContext context) {
        if (!context.computerOnline() || context.recommendation().risk() != ActionRisk.COSTLY
                || !context.policy().shouldAct(context.recommendation(), context.waitingTicks(), context.responseWindow())) return null;
        ActionResult result = context.recommendation().action().execute(context.spacecraft());
        if (!result.successful()) return null;
        return new ResponseDecision(true, "AUTOPILOT", result, false, context.escalated());
    }
}

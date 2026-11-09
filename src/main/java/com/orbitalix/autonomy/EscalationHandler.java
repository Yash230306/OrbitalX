package com.orbitalix.autonomy;

import com.orbitalix.api.*;
import com.orbitalix.event.EventBus;

public final class EscalationHandler extends ResponseHandler {
    @Override protected ResponseDecision tryHandle(ResponseContext context) {
        String message = context.recommendation().title() + " needs human action";
        if (context.escalated()) message += " after repeated onboard failure";
        context.eventBus().publish(EventBus.MISSION_EVENTS, new MissionEvent(context.tick(), EventType.ESCALATION,
                Severity.HIGH, message));
        return new ResponseDecision(false, "HUMAN", null, true, context.escalated());
    }
}

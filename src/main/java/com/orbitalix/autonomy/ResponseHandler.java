package com.orbitalix.autonomy;

public abstract class ResponseHandler {
    private ResponseHandler next;
    public final ResponseHandler setNext(ResponseHandler handler) { next = handler; return handler; }
    public final ResponseDecision handle(ResponseContext context) {
        ResponseDecision decision = tryHandle(context);
        if (decision != null) return decision;
        return pass(context);
    }
    protected final ResponseDecision pass(ResponseContext context) {
        if (next != null) return next.handle(context);
        return new ResponseDecision(false, "NONE", null, true, context.escalated());
    }
    protected abstract ResponseDecision tryHandle(ResponseContext context);
}

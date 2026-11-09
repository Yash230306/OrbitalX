package com.orbitalix.action;

import com.orbitalix.api.ActionRisk;
import java.util.Locale;

public abstract class AbstractCorrectiveAction implements CorrectiveAction {
    private final String id;
    private final String description;
    private final ActionRisk risk;
    protected AbstractCorrectiveAction(String id, String description, ActionRisk risk) {
        this.id = id;
        this.description = description;
        this.risk = risk;
    }
    @Override public final String id() { return id; }
    @Override public final String description() { return description; }
    @Override public final ActionRisk risk() { return risk; }
    protected final ActionResult success(String message) { return ActionResult.success(message); }
    protected final ActionResult failure(String message) { return ActionResult.failure(message); }
    protected static String idFor(Class<?> type) {
        return type.getSimpleName().replaceAll("Action$", "").replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase(Locale.ROOT);
    }
}

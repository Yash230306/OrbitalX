package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class RestoreSafeLimitsAction extends AbstractCorrectiveAction {
    public RestoreSafeLimitsAction() { super(idFor(RestoreSafeLimitsAction.class), "Restore safe operating limits", ActionRisk.COSTLY); }
    @Override public ActionResult execute(Spacecraft craft) {
        craft.limits().restoreSafeDefaults();
        if (craft.throttle() > craft.limits().get(com.orbitalix.api.LimitType.MAX_THROTTLE))
            craft.setThrottle((int) craft.limits().get(com.orbitalix.api.LimitType.MAX_THROTTLE));
        return success("Safe operating limits restored");
    }
}

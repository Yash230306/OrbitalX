package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class RestartCoolantPumpAction extends AbstractCorrectiveAction {
    public RestartCoolantPumpAction() { super(idFor(RestartCoolantPumpAction.class), "Restart coolant pump (3 ticks)", ActionRisk.SAFE); }
    @Override public ActionResult execute(Spacecraft craft) {
        if (craft.coolantRestartTicks() > 0) return success("Coolant pump restart already in progress");
        craft.restartCoolantPump();
        return success("Coolant pump restart started; cooling resumes in 3 ticks");
    }
}

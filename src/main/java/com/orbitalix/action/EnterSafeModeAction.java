package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
import com.orbitalix.state.SafeModeState;
public final class EnterSafeModeAction extends AbstractCorrectiveAction {
    public EnterSafeModeAction() { super(idFor(EnterSafeModeAction.class), "Enter safe mode", ActionRisk.COSTLY); }
    @Override public ActionResult execute(Spacecraft craft) {
        craft.setState(new SafeModeState());
        craft.setThrottle(Math.min(craft.throttle(), 25));
        craft.setNonEssentialSystemsOn(false);
        return success("Spacecraft entered safe mode");
    }
}

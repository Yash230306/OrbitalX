package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class AbortBurnAction extends AbstractCorrectiveAction {
    public AbortBurnAction() { super(idFor(AbortBurnAction.class), "Abort propulsion burn", ActionRisk.COSTLY); }
    @Override public ActionResult execute(Spacecraft craft) { craft.setThrottle(0); return success("Burn aborted"); }
}

package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class ActivateRadiatorsAction extends AbstractCorrectiveAction {
    public ActivateRadiatorsAction() { super(idFor(ActivateRadiatorsAction.class), "Activate radiators", ActionRisk.SAFE); }
    @Override public ActionResult execute(Spacecraft craft) { craft.setRadiatorsActive(true); return success("Radiators activated"); }
}

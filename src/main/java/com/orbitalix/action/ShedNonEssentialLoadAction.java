package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class ShedNonEssentialLoadAction extends AbstractCorrectiveAction {
    public ShedNonEssentialLoadAction() { super(idFor(ShedNonEssentialLoadAction.class), "Shed non-essential power load", ActionRisk.SAFE); }
    @Override public ActionResult execute(Spacecraft craft) { craft.setNonEssentialSystemsOn(false); return success("Non-essential load shed"); }
}

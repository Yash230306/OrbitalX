package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class ReorientSolarPanelsAction extends AbstractCorrectiveAction {
    public ReorientSolarPanelsAction() { super(idFor(ReorientSolarPanelsAction.class), "Reorient solar panels", ActionRisk.SAFE); }
    @Override public ActionResult execute(Spacecraft craft) { craft.setSolarPanelsReoriented(true); return success("Solar panels reoriented; partial generation restored"); }
}

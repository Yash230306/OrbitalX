package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class EnterRadiationShelterAction extends AbstractCorrectiveAction {
    public EnterRadiationShelterAction() { super(idFor(EnterRadiationShelterAction.class), "Enter radiation shelter", ActionRisk.COSTLY); }
    @Override public ActionResult execute(Spacecraft craft) {
        craft.setCrewSheltered(true);
        craft.setNonEssentialSystemsOn(false);
        craft.setThrottle(Math.min(craft.throttle(), 50));
        return success("Crew shelter secured and throttle capped");
    }
}

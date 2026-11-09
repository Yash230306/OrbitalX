package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class EvasiveManeuverAction extends AbstractCorrectiveAction {
    public EvasiveManeuverAction() { super(idFor(EvasiveManeuverAction.class), "Execute evasive maneuver (4 fuel)", ActionRisk.COSTLY); }
    @Override public ActionResult execute(Spacecraft craft) {
        if (!craft.spendFuel(4)) return failure("Insufficient main fuel for evasive maneuver");
        craft.updateCourseError(1);
        return success("Evasive maneuver completed; 4 fuel consumed");
    }
}

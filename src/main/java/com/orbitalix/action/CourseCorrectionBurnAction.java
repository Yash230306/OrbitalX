package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class CourseCorrectionBurnAction extends AbstractCorrectiveAction {
    public CourseCorrectionBurnAction() { super(idFor(CourseCorrectionBurnAction.class), "Perform course correction burn (3 fuel)", ActionRisk.COSTLY); }
    @Override public ActionResult execute(Spacecraft craft) {
        if (!craft.spendFuel(3)) return failure("Insufficient main fuel for course correction");
        craft.recalibrateCourse();
        return success("Course corrected; 3 fuel consumed");
    }
}

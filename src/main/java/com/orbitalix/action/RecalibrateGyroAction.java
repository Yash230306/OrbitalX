package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.api.FaultType;
import com.orbitalix.model.Spacecraft;
public final class RecalibrateGyroAction extends AbstractCorrectiveAction {
    public RecalibrateGyroAction() { super(idFor(RecalibrateGyroAction.class), "Recalibrate gyro", ActionRisk.SAFE); }
    @Override public ActionResult execute(Spacecraft craft) {
        craft.recalibrateCourse();
        craft.clearFault(FaultType.SENSOR_DRIFT);
        return success("Gyro recalibrated");
    }
}

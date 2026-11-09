package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.api.FaultType;
import com.orbitalix.model.Spacecraft;
public final class SwitchToBackupSensorAction extends AbstractCorrectiveAction {
    public SwitchToBackupSensorAction() { super(idFor(SwitchToBackupSensorAction.class), "Switch to backup attitude sensor", ActionRisk.SAFE); }
    @Override public ActionResult execute(Spacecraft craft) {
        craft.setBackupSensorActive(true);
        craft.clearFault(FaultType.SENSOR_DRIFT);
        return success("Backup attitude sensor selected");
    }
}

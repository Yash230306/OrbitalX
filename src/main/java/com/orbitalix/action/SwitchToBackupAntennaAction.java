package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.api.FaultType;
import com.orbitalix.model.Spacecraft;
public final class SwitchToBackupAntennaAction extends AbstractCorrectiveAction {
    public SwitchToBackupAntennaAction() { super(idFor(SwitchToBackupAntennaAction.class), "Switch to backup antenna", ActionRisk.SAFE); }
    @Override public ActionResult execute(Spacecraft craft) {
        craft.setBackupAntennaActive(true);
        craft.clearFault(FaultType.DROPOUT);
        return success("Backup antenna selected");
    }
}

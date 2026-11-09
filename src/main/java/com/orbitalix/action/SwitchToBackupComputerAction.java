package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.api.FaultType;
import com.orbitalix.model.Spacecraft;
public final class SwitchToBackupComputerAction extends AbstractCorrectiveAction {
    public SwitchToBackupComputerAction() { super(idFor(SwitchToBackupComputerAction.class), "Switch to backup computer", ActionRisk.COSTLY); }
    @Override public ActionResult execute(Spacecraft craft) {
        craft.clearFault(FaultType.COMPUTER_FAULT);
        craft.switchToBackupComputer();
        return success("Backup flight computer engaged");
    }
}

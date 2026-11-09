package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class SwitchToBackupEngineAction extends AbstractCorrectiveAction {
    public SwitchToBackupEngineAction() { super(idFor(SwitchToBackupEngineAction.class), "Switch to backup engine", ActionRisk.COSTLY); }
    @Override public ActionResult execute(Spacecraft craft) { craft.setBackupEngineActive(true); return success("Backup engine engaged at 85% efficiency"); }
}

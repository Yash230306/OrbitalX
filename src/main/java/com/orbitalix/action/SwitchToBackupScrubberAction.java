package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class SwitchToBackupScrubberAction extends AbstractCorrectiveAction {
    public SwitchToBackupScrubberAction() { super(idFor(SwitchToBackupScrubberAction.class), "Switch to backup scrubber", ActionRisk.COSTLY); }
    @Override public ActionResult execute(Spacecraft craft) { craft.setBackupScrubberActive(true); return success("Backup CO2 scrubber engaged"); }
}

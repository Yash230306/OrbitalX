package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.api.FaultType;
import com.orbitalix.model.Spacecraft;
public final class RebootComputerAction extends AbstractCorrectiveAction {
    public RebootComputerAction() { super(idFor(RebootComputerAction.class), "Reboot onboard computer", ActionRisk.SAFE); }
    @Override public ActionResult execute(Spacecraft craft) {
        craft.clearFault(FaultType.COMPUTER_FAULT);
        craft.rebootComputer();
        return success("Computer reboot started; telemetry is unavailable for 3 ticks");
    }
}

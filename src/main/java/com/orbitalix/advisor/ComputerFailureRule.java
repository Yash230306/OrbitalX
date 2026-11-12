package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.CrewTaskType;
import com.orbitalix.api.FaultType;
// Declares MANUAL_REBOOT as the required crew task — no string-switch needed in CrewHandler.
public final class ComputerFailureRule extends ConditionRule {
    public ComputerFailureRule() { super("computer-failure", "Flight computer errors", "Computer error rate requires a reboot.", 89,
            craft -> craft.isFaultActive(FaultType.COMPUTER_FAULT) || craft.computerError() >= 10, RebootComputerAction::new,
            CrewTaskType.MANUAL_REBOOT); }
}

package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.FaultType;
public final class ComputerFailureRule extends ConditionRule {
    public ComputerFailureRule() { super("computer-failure", "Flight computer errors", "Computer error rate requires a reboot.", 89,
            craft -> craft.isFaultActive(FaultType.COMPUTER_FAULT) || craft.computerError() >= 10, RebootComputerAction::new); }
}

package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.FaultType;
public final class CoolantFailureRule extends ConditionRule {
    public CoolantFailureRule() { super("coolant-failure", "Coolant pump failed", "Radiators cannot cool the craft until the pump restarts.", 88,
            craft -> craft.isFaultActive(FaultType.COOLANT_FAILURE), RestartCoolantPumpAction::new); }
}

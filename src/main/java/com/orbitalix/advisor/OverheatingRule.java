package com.orbitalix.advisor;
import com.orbitalix.action.*;
public final class OverheatingRule extends ConditionRule {
    public OverheatingRule() { super("overheating", "Engine overheating", "Temperature exceeded the warning limit.", 85,
            craft -> craft.temperature() >= craft.limits().get(com.orbitalix.api.LimitType.MAX_TEMPERATURE), ActivateRadiatorsAction::new); }
}

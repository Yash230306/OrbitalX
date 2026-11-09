package com.orbitalix.advisor;
import com.orbitalix.action.*;
public final class PowerShortageRule extends ConditionRule {
    public PowerShortageRule() { super("power-shortage", "Power reserve low", "Battery is below its safe operating threshold.", 80,
            craft -> craft.battery() < craft.limits().get(com.orbitalix.api.LimitType.MIN_BATTERY), ShedNonEssentialLoadAction::new); }
}

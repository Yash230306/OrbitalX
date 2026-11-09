package com.orbitalix.advisor;
import com.orbitalix.action.*;
public final class FuelLowRule extends ConditionRule {
    public FuelLowRule() { super("fuel-low", "Fuel margin low", "Remaining fuel may not cover the planned mission.", 90,
            craft -> craft.fuel() < 25, ReduceThrottleAction::new); }
}

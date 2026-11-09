package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.FaultType;
public final class HullBreachRule extends ConditionRule {
    public HullBreachRule() { super("hull-breach", "Cabin pressure loss", "Hull breach is reducing cabin pressure.", 100,
            craft -> craft.isFaultActive(FaultType.HULL_BREACH), SealBreachAction::new); }
}

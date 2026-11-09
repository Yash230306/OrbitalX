package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.FaultType;
public final class DebrisThreatRule extends ConditionRule {
    public DebrisThreatRule() { super("debris-threat", "Debris on collision course", "A debris threat requires an evasive maneuver.", 98,
            craft -> craft.isFaultActive(FaultType.DEBRIS_THREAT), EvasiveManeuverAction::new); }
}

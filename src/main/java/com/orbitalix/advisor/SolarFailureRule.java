package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.FaultType;
public final class SolarFailureRule extends ConditionRule {
    public SolarFailureRule() { super("solar-failure", "Solar array failure", "Power generation is unavailable.", 83,
            craft -> craft.isFaultActive(FaultType.SOLAR_FAILURE), ReorientSolarPanelsAction::new); }
}

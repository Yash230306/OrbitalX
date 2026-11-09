package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.FaultType;
public final class RadiationStormRule extends ConditionRule {
    public RadiationStormRule() { super("radiation-storm", "Radiation exposure", "Radiation is above the crew safety threshold.", 92,
            craft -> craft.radiation() > 3 || craft.isFaultActive(FaultType.RADIATION_STORM), EnterRadiationShelterAction::new); }
}

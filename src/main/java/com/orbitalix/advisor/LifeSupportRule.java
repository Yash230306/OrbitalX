package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.FaultType;
public final class LifeSupportRule extends ConditionRule {
    public LifeSupportRule() { super("life-support", "Life support reserve", "Cabin oxygen or CO2 is approaching an unsafe level.", 95,
            craft -> craft.oxygen() < 25 || craft.co2() > 4 || craft.isFaultActive(FaultType.SCRUBBER_FAILURE), SwitchToBackupScrubberAction::new); }
}

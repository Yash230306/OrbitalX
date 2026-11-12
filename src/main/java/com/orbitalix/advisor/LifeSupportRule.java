package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.CrewTaskType;
import com.orbitalix.api.FaultType;
// Declares MEDICAL_CARE as the required crew task — no string-switch needed in CrewHandler.
public final class LifeSupportRule extends ConditionRule {
    public LifeSupportRule() { super("life-support", "Life support reserve", "Cabin oxygen or CO2 is approaching an unsafe level.", 95,
            craft -> craft.oxygen() < 25 || craft.co2() > 4 || craft.isFaultActive(FaultType.SCRUBBER_FAILURE), SwitchToBackupScrubberAction::new,
            CrewTaskType.MEDICAL_CARE); }
}

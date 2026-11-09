package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.FaultType;
public final class CommunicationBlackoutRule extends ConditionRule {
    public CommunicationBlackoutRule() { super("communication-blackout", "Communication blackout", "Primary antenna signal is lost.", 70,
            craft -> craft.isFaultActive(FaultType.DROPOUT), SwitchToBackupAntennaAction::new); }
}

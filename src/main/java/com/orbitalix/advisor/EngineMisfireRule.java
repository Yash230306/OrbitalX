package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.FaultType;
public final class EngineMisfireRule extends ConditionRule {
    public EngineMisfireRule() { super("engine-misfire", "Engine misfire", "Thrust efficiency is reduced.", 78,
            craft -> craft.isFaultActive(FaultType.ENGINE_MISFIRE), SwitchToBackupEngineAction::new); }
}

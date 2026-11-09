package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.FaultType;
public final class GyroDriftRule extends ConditionRule {
    public GyroDriftRule() { super("gyro-drift", "Attitude sensor drift", "Pointing error is accumulating.", 76,
            craft -> craft.isFaultActive(FaultType.SENSOR_DRIFT) || craft.courseError() > 5, RecalibrateGyroAction::new); }
}

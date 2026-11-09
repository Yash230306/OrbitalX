package com.orbitalix.advisor;
import com.orbitalix.action.*;
import com.orbitalix.api.FaultType;
public final class NavigationDriftRule extends ConditionRule {
    public NavigationDriftRule() { super("navigation-drift", "Navigation drift", "Course error is slowing mission progress.", 75,
            craft -> craft.isFaultActive(FaultType.NAV_DRIFT) || craft.courseError() > 10, CourseCorrectionBurnAction::new); }
}

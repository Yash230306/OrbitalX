package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class ReduceThrottleAction extends AbstractCorrectiveAction {
    private final int target;
    public ReduceThrottleAction() { this(60); }
    public ReduceThrottleAction(int target) {
        super(idFor(ReduceThrottleAction.class), "Reduce throttle to " + target + "%", ActionRisk.COSTLY);
        if (target < 0 || target > 100) throw new IllegalArgumentException("Throttle must be 0..100");
        this.target = target;
    }
    @Override public ActionResult execute(Spacecraft craft) {
        int permitted = (int) Math.min(target, craft.limits().get(com.orbitalix.api.LimitType.MAX_THROTTLE));
        if (craft.throttle() <= permitted) return success("Throttle already within target");
        craft.setThrottle(permitted);
        return success("Throttle reduced to " + permitted + "%");
    }
}

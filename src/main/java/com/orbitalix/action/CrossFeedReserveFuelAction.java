package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class CrossFeedReserveFuelAction extends AbstractCorrectiveAction {
    public CrossFeedReserveFuelAction() { super(idFor(CrossFeedReserveFuelAction.class), "Cross-feed reserve fuel", ActionRisk.COSTLY); }
    @Override public ActionResult execute(Spacecraft craft) {
        if (craft.reserveCrossFeed()) return success("Reserve fuel cross-feed is already enabled");
        craft.crossFeedReserveFuel();
        return success("Reserve fuel cross-feed enabled");
    }
}

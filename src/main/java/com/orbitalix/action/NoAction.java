package com.orbitalix.action;

import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;

/** Null Object implementation makes an absent recommendation safe to execute. */
public final class NoAction extends AbstractCorrectiveAction {
    public NoAction() { super("none", "No corrective action", ActionRisk.SAFE); }
    @Override public ActionResult execute(Spacecraft spacecraft) { return success("No action required"); }
}

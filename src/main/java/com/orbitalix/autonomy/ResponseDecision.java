package com.orbitalix.autonomy;

import com.orbitalix.action.ActionResult;

public record ResponseDecision(boolean handled, String decidedBy, ActionResult result,
                              boolean needsHuman, boolean escalated) {}

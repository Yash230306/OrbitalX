package com.orbitalix.action;

import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;

/** Command contract separates mission-control decisions from spacecraft effects. */
public interface CorrectiveAction {
    String id();
    String description();
    ActionRisk risk();
    ActionResult execute(Spacecraft spacecraft);
}

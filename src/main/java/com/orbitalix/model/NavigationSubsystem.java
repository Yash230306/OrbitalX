package com.orbitalix.model;

import com.orbitalix.api.FaultType;
import com.orbitalix.sensor.CourseSensor;
import java.util.List;

public final class NavigationSubsystem extends Subsystem {
    public NavigationSubsystem(Spacecraft owner) {
        super("Navigation", List.of(new CourseSensor("course-error", owner::courseError)));
    }
    @Override protected void applyPhysics(Spacecraft craft, SpaceEnvironment environment) {
        if (hasFault(FaultType.NAV_DRIFT)) craft.updateCourseError(0.7);
    }
    @Override public NavigationSubsystem copy(Spacecraft newOwner) {
        NavigationSubsystem copy = new NavigationSubsystem(newOwner); copyFaultsTo(copy); return copy;
    }
}

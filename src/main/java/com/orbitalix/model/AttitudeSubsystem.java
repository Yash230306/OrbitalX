package com.orbitalix.model;

import com.orbitalix.api.FaultType;
import com.orbitalix.sensor.GyroSensor;
import java.util.List;

public final class AttitudeSubsystem extends Subsystem {
    public AttitudeSubsystem(Spacecraft owner) {
        super("Attitude", List.of(new GyroSensor("primary-gyro", owner::courseError)));
    }
    @Override protected void applyPhysics(Spacecraft craft, SpaceEnvironment environment) {
        craft.updateCourseError(0.05 + (hasFault(FaultType.SENSOR_DRIFT) ? 0.8 : 0));
    }
    @Override public AttitudeSubsystem copy(Spacecraft newOwner) {
        AttitudeSubsystem copy = new AttitudeSubsystem(newOwner); copyFaultsTo(copy); return copy;
    }
}

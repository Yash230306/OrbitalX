package com.orbitalix.model;

import com.orbitalix.api.FaultType;
import com.orbitalix.sensor.SignalSensor;
import java.util.List;

public final class CommunicationSubsystem extends Subsystem {
    public CommunicationSubsystem(Spacecraft owner) {
        super("Communication", List.of(new SignalSensor("uplink-signal", owner::signalStrength)));
    }
    @Override protected void applyPhysics(Spacecraft craft, SpaceEnvironment environment) {
        if (hasFault(FaultType.DROPOUT) && !craft.backupAntennaActive()) craft.setSignalStrength(0);
        else craft.setSignalStrength(Math.max(0.35, 1 - craft.courseError() / 100));
    }
    @Override public CommunicationSubsystem copy(Spacecraft newOwner) {
        CommunicationSubsystem copy = new CommunicationSubsystem(newOwner); copyFaultsTo(copy); return copy;
    }
}

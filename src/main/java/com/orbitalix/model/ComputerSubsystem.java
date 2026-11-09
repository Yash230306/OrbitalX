package com.orbitalix.model;

import com.orbitalix.api.FaultType;
import com.orbitalix.sensor.ComputerSensor;
import java.util.List;

public final class ComputerSubsystem extends Subsystem {
    public ComputerSubsystem(Spacecraft owner) {
        super("Computer", List.of(new ComputerSensor("computer-errors", owner::computerError)));
    }
    @Override protected void applyPhysics(Spacecraft craft, SpaceEnvironment environment) {
        if (hasFault(FaultType.COMPUTER_FAULT) || environment.radiationStorm()) craft.addComputerError();
        if (craft.computerError() >= 15) craft.crashComputer();
        if (craft.computerOffline()) craft.advanceComputerFreeze();
    }
    @Override public ComputerSubsystem copy(Spacecraft newOwner) {
        ComputerSubsystem copy = new ComputerSubsystem(newOwner); copyFaultsTo(copy); return copy;
    }
}

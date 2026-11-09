package com.orbitalix.model;

import com.orbitalix.api.FaultType;
import com.orbitalix.api.LimitType;
import java.util.Map;
import java.util.Set;

/** Snapshot transfer object keeps the simulation API boundary separate from mutable model internals. */
public record SpacecraftTelemetry(int throttle, double fuel, double reserveFuel, double battery, double oxygen,
        double sealant, double temperature, double pressure, double co2, double radiation, double courseError,
        double signalStrength, double engineEfficiency, int computerError, int computerFreeze,
        int coolantRestartTicks,
        boolean nonEssentialSystemsOn, boolean radiatorsActive, boolean coolantWorking,
        boolean backupAntennaActive, boolean backupEngineActive, boolean backupScrubberActive,
        boolean reserveCrossFeed, boolean solarPanelsReoriented, boolean crewSheltered,
        Set<FaultType> activeFaults, Map<LimitType, Double> limits, boolean overridden) {
    public SpacecraftTelemetry {
        activeFaults = Set.copyOf(activeFaults);
        limits = Map.copyOf(limits);
        if (throttle < 0 || throttle > 100 || computerError < 0 || computerFreeze < 0 || coolantRestartTicks < 0)
            throw new IllegalArgumentException("Invalid spacecraft telemetry");
        double[] measurements = {fuel, reserveFuel, battery, oxygen, sealant, temperature, pressure, co2, radiation,
                courseError, signalStrength, engineEfficiency};
        for (double measurement : measurements)
            if (!Double.isFinite(measurement) || measurement < 0)
                throw new IllegalArgumentException("Telemetry values must be finite and non-negative");
    }
}

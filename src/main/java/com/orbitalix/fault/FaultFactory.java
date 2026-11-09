package com.orbitalix.fault;

import com.orbitalix.api.FaultType;

/** Factory Method centralizes concrete fault construction. */
public final class FaultFactory {
    private FaultFactory() {}
    public static Fault create(FaultType type) {
        return switch (type) {
            case SENSOR_DRIFT, SPIKE, STUCK_VALUE -> new SensorFault(type);
            case DROPOUT -> new DropoutFault();
            case FUEL_LEAK -> new FuelLeakFault();
            case OVERHEAT -> new OverheatFault();
            case COOLANT_FAILURE -> new CoolantFailureFault();
            case SOLAR_FAILURE -> new SolarFailureFault();
            case SCRUBBER_FAILURE -> new ScrubberFailureFault();
            case HULL_BREACH -> new HullBreachFault();
            case RADIATION_STORM -> new RadiationStormFault();
            case NAV_DRIFT -> new NavDriftFault();
            case DEBRIS_THREAT -> new DebrisThreatFault();
            case COMPUTER_FAULT -> new ComputerFault();
            case ENGINE_MISFIRE -> new EngineMisfireFault();
        };
    }
}

package com.orbitalix.simulation;

import com.orbitalix.api.FaultType;
import java.util.List;

public final class ScenarioFactory {
    private static final List<String> NAMES = List.of("Free Flight", "Fuel Leak", "Engine Trouble",
            "Power Crisis", "Lost in Space", "Antenna Blackout", "Hull Breach", "Solar Storm",
            "Debris Alert", "Computer Crash", "Cascading Failure", "Endless Chaos");
    public List<String> names() { return NAMES; }
    public Scenario create(int index) {
        if (index < 0 || index >= NAMES.size()) throw new IllegalArgumentException("Unknown scenario: " + index);
        List<Scenario.Trigger> triggers = switch (index) {
            case 1 -> List.of(trigger(20, "Propulsion", FaultType.FUEL_LEAK));
            case 2 -> List.of(trigger(20, "Propulsion", FaultType.ENGINE_MISFIRE),
                    trigger(35, "Thermal", FaultType.OVERHEAT), trigger(50, "Thermal", FaultType.COOLANT_FAILURE));
            case 3 -> List.of(trigger(15, "Power", FaultType.SOLAR_FAILURE));
            case 4 -> List.of(trigger(20, "Attitude", FaultType.SENSOR_DRIFT),
                    trigger(35, "Navigation", FaultType.NAV_DRIFT));
            case 5 -> List.of(trigger(30, "Communication", FaultType.DROPOUT));
            case 6 -> List.of(trigger(25, "Life Support", FaultType.HULL_BREACH),
                    trigger(45, "Life Support", FaultType.SCRUBBER_FAILURE));
            case 7 -> List.of(trigger(20, "Environment", FaultType.RADIATION_STORM));
            case 8 -> List.of(trigger(15, "Environment", FaultType.DEBRIS_THREAT),
                    trigger(55, "Environment", FaultType.DEBRIS_THREAT));
            case 9 -> List.of(trigger(25, "Computer", FaultType.COMPUTER_FAULT),
                    trigger(30, "Environment", FaultType.RADIATION_STORM));
            case 10 -> List.of(trigger(15, "Propulsion", FaultType.FUEL_LEAK),
                    trigger(25, "Thermal", FaultType.OVERHEAT), trigger(35, "Environment", FaultType.RADIATION_STORM),
                    trigger(45, "Environment", FaultType.DEBRIS_THREAT), trigger(55, "Computer", FaultType.COMPUTER_FAULT),
                    trigger(65, "Life Support", FaultType.HULL_BREACH));
            default -> List.of();
        };
        return new Scenario(index, NAMES.get(index), triggers);
    }
    private static Scenario.Trigger trigger(int tick, String target, FaultType type) {
        return new Scenario.Trigger(tick, target, type);
    }
}

package com.orbitalix.fault;

import com.orbitalix.api.FaultType;

/** One sensor fault implementation represents drift, spike, and stuck-value decorator failures. */
public final class SensorFault extends Fault {
    private final FaultType type;
    public SensorFault(FaultType type) { this(type, 0); }
    public SensorFault(FaultType type, int duration) {
        super(duration);
        if (type != FaultType.SENSOR_DRIFT && type != FaultType.SPIKE && type != FaultType.STUCK_VALUE)
            throw new IllegalArgumentException("Not a sensor fault: " + type);
        this.type = type;
    }
    @Override public FaultType type() { return type; }
    @Override public SensorFault copy() { SensorFault copy = new SensorFault(type, durationTicks()); copyLifecycleTo(copy); return copy; }
}

package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class GyroDriftFault extends Fault {
    public GyroDriftFault() {}
    public GyroDriftFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.SENSOR_DRIFT; }
    @Override public String describe() { return "primary gyro bias drift"; }
    @Override public GyroDriftFault copy() { GyroDriftFault copy = new GyroDriftFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

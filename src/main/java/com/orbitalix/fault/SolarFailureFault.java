package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class SolarFailureFault extends Fault {
    public SolarFailureFault() {}
    public SolarFailureFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.SOLAR_FAILURE; }
    @Override public SolarFailureFault copy() { SolarFailureFault copy = new SolarFailureFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

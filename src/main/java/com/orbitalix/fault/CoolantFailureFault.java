package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class CoolantFailureFault extends Fault {
    public CoolantFailureFault() {}
    public CoolantFailureFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.COOLANT_FAILURE; }
    @Override public CoolantFailureFault copy() { CoolantFailureFault copy = new CoolantFailureFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

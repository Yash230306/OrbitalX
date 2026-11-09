package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class EngineMisfireFault extends Fault {
    public EngineMisfireFault() {}
    public EngineMisfireFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.ENGINE_MISFIRE; }
    @Override public EngineMisfireFault copy() { EngineMisfireFault copy = new EngineMisfireFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

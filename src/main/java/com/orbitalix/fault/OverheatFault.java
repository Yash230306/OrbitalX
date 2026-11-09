package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class OverheatFault extends Fault {
    public OverheatFault() {}
    public OverheatFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.OVERHEAT; }
    @Override public OverheatFault copy() { OverheatFault copy = new OverheatFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

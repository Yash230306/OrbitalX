package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class HullBreachFault extends Fault {
    public HullBreachFault() {}
    public HullBreachFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.HULL_BREACH; }
    @Override public HullBreachFault copy() { HullBreachFault copy = new HullBreachFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

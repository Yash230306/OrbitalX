package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class NavDriftFault extends Fault {
    public NavDriftFault() {}
    public NavDriftFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.NAV_DRIFT; }
    @Override public NavDriftFault copy() { NavDriftFault copy = new NavDriftFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

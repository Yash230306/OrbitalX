package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class DebrisThreatFault extends Fault {
    public DebrisThreatFault() { super(30); }
    public DebrisThreatFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.DEBRIS_THREAT; }
    @Override public DebrisThreatFault copy() { DebrisThreatFault copy = new DebrisThreatFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

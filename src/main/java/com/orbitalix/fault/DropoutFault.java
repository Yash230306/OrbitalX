package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class DropoutFault extends Fault {
    public DropoutFault() {}
    public DropoutFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.DROPOUT; }
    @Override public DropoutFault copy() { DropoutFault copy = new DropoutFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class ComputerFault extends Fault {
    public ComputerFault() {}
    public ComputerFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.COMPUTER_FAULT; }
    @Override public ComputerFault copy() { ComputerFault copy = new ComputerFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

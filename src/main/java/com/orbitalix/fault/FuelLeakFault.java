package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class FuelLeakFault extends Fault {
    public FuelLeakFault() {}
    public FuelLeakFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.FUEL_LEAK; }
    @Override public FuelLeakFault copy() { FuelLeakFault copy = new FuelLeakFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class RadiationStormFault extends Fault {
    public RadiationStormFault() { super(25); }
    public RadiationStormFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.RADIATION_STORM; }
    @Override public RadiationStormFault copy() { RadiationStormFault copy = new RadiationStormFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

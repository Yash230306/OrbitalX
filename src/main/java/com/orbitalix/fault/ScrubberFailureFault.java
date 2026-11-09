package com.orbitalix.fault;
import com.orbitalix.api.FaultType;
public final class ScrubberFailureFault extends Fault {
    public ScrubberFailureFault() {}
    public ScrubberFailureFault(int duration) { super(duration); }
    @Override public FaultType type() { return FaultType.SCRUBBER_FAILURE; }
    @Override public ScrubberFailureFault copy() { ScrubberFailureFault copy = new ScrubberFailureFault(durationTicks()); copyLifecycleTo(copy); return copy; }
}

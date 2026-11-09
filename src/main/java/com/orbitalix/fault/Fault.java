package com.orbitalix.fault;

import com.orbitalix.api.Copyable;
import com.orbitalix.api.FaultType;

/** Template Method base for applying and expiring spacecraft faults. */
public abstract class Fault implements Copyable<Fault> {
    private final int durationTicks;
    private boolean active;
    private int ticksRemaining;

    protected Fault() { this(0); }
    protected Fault(int durationTicks) {
        if (durationTicks < 0) throw new IllegalArgumentException("Fault duration cannot be negative");
        this.durationTicks = durationTicks;
    }

    public abstract FaultType type();
    public String describe() { return type().name().replace('_', ' ').toLowerCase(); }
    public final synchronized boolean isActive() { return active; }
    public final synchronized int ticksRemaining() { return ticksRemaining; }

    public synchronized void apply(FaultTarget target) {
        if (target == null) throw new IllegalArgumentException("Fault target is required");
        target.activateFault(type(), durationTicks);
        active = true;
        ticksRemaining = durationTicks;
        onApplied(target);
    }

    public synchronized void tick(FaultTarget target) {
        if (!active || durationTicks == 0) return;
        ticksRemaining--;
        if (ticksRemaining <= 0) {
            active = false;
            target.clearFault(type());
            onExpired(target);
        }
    }

    protected void onApplied(FaultTarget target) {}
    protected void onExpired(FaultTarget target) {}
    protected final int durationTicks() { return durationTicks; }
    protected final synchronized void copyLifecycleTo(Fault result) {
        result.active = active;
        result.ticksRemaining = ticksRemaining;
    }
}

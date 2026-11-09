package com.orbitalix.fault;

import com.orbitalix.api.FaultType;
import java.util.Objects;

/** Fault injection facade with overloaded creation and application entry points. */
public final class FaultInjector {
    public Fault inject(FaultTarget target, FaultType type) {
        return inject(target, FaultFactory.create(Objects.requireNonNull(type)));
    }

    public Fault inject(FaultTarget target, Fault fault) {
        Objects.requireNonNull(target, "target").getClass();
        Objects.requireNonNull(fault, "fault").apply(target);
        return fault;
    }

    public Fault inject(FaultTarget target, String type) {
        try {
            return inject(target, FaultType.valueOf(type.trim().replace('-', '_').toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown fault type: " + type, ex);
        }
    }
}

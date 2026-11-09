package com.orbitalix.fault;

import com.orbitalix.api.FaultType;

/** Boundary implemented by spacecraft models that accept and clear runtime failures. */
public interface FaultTarget {
    void activateFault(FaultType type, int durationTicks);
    boolean isFaultActive(FaultType type);
    void clearFault(FaultType type);
}

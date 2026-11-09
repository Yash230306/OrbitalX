package com.orbitalix.state;

import com.orbitalix.api.HealthStatus;

public final class SafeModeState implements SpacecraftState {
    @Override public String name() { return "SAFE_MODE"; }
    @Override public SpacecraftState transition(HealthStatus health) { return this; }
    @Override public SafeModeState copy() { return new SafeModeState(); }
}

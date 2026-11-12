package com.orbitalix.state;

import com.orbitalix.api.HealthStatus;
import com.orbitalix.gui.theme.Theme;
import java.awt.Color;

public final class SafeModeState implements SpacecraftState {
    @Override public String name() { return "SAFE_MODE"; }
    @Override public SpacecraftState transition(HealthStatus health) {
        return health == HealthStatus.NOMINAL ? new NominalState() : this;
    }
    @Override public double maxSafeThrottle() { return 0.0; }
    @Override public boolean allowsManualOverride() { return false; }
    @Override public boolean isSafeMode() { return true; }
    @Override public boolean isNominal() { return false; }
    @Override public String description() { return "System lockdown active; propulsion isolated for autonomous self-recovery."; }
    @Override public Color badgeColor(Theme theme) { return theme.critical(); }
    @Override public SafeModeState copy() { return new SafeModeState(); }
}

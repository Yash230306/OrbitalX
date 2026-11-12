package com.orbitalix.state;

import com.orbitalix.api.HealthStatus;
import com.orbitalix.gui.theme.Theme;
import java.awt.Color;

public final class CriticalState implements SpacecraftState {
    @Override public String name() { return "CRITICAL"; }
    @Override public SpacecraftState transition(HealthStatus health) {
        return health == HealthStatus.CRITICAL ? this : StateTransitions.from(health);
    }
    @Override public double maxSafeThrottle() { return 50.0; }
    @Override public boolean allowsManualOverride() { return true; }
    @Override public boolean isSafeMode() { return false; }
    @Override public boolean isNominal() { return false; }
    @Override public String description() { return "Critical subsystem emergency; priority recovery protocols engaged."; }
    @Override public Color badgeColor(Theme theme) { return theme.critical(); }
    @Override public CriticalState copy() { return new CriticalState(); }
}

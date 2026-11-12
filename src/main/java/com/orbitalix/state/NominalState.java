package com.orbitalix.state;

import com.orbitalix.api.HealthStatus;
import com.orbitalix.gui.theme.Theme;
import java.awt.Color;

public final class NominalState implements SpacecraftState {
    @Override public String name() { return "NOMINAL"; }
    @Override public SpacecraftState transition(HealthStatus health) {
        return health == HealthStatus.NOMINAL ? this : StateTransitions.from(health);
    }
    @Override public double maxSafeThrottle() { return 100.0; }
    @Override public boolean allowsManualOverride() { return true; }
    @Override public boolean isSafeMode() { return false; }
    @Override public boolean isNominal() { return true; }
    @Override public String description() { return "All flight systems operating within standard nominal parameters."; }
    @Override public Color badgeColor(Theme theme) { return theme.nominal(); }
    @Override public NominalState copy() { return new NominalState(); }
}

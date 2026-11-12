package com.orbitalix.state;

import com.orbitalix.api.HealthStatus;
import com.orbitalix.gui.theme.Theme;
import java.awt.Color;

public final class WarningState implements SpacecraftState {
    @Override public String name() { return "WARNING"; }
    @Override public SpacecraftState transition(HealthStatus health) {
        return health == HealthStatus.WARNING ? this : StateTransitions.from(health);
    }
    @Override public double maxSafeThrottle() { return 80.0; }
    @Override public boolean allowsManualOverride() { return true; }
    @Override public boolean isSafeMode() { return false; }
    @Override public boolean isNominal() { return false; }
    @Override public String description() { return "Telemetry variance detected; supervisory alerts and cautions active."; }
    @Override public Color badgeColor(Theme theme) { return theme.warning(); }
    @Override public WarningState copy() { return new WarningState(); }
}

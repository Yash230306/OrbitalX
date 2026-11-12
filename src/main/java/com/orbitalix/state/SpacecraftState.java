package com.orbitalix.state;

import com.orbitalix.api.Copyable;
import com.orbitalix.api.HealthStatus;
import com.orbitalix.gui.theme.Theme;
import java.awt.Color;

/**
 * State Pattern (GoF) representing the behavioral operating state of the spacecraft.
 * Encapsulates state-specific constraints, safety limits, recovery requirements, and visual status.
 */
public interface SpacecraftState extends Copyable<SpacecraftState> {
    /** Distinctive identifier of this operational state. */
    String name();

    /** Polymorphic state transition based on aggregate spacecraft health. */
    SpacecraftState transition(HealthStatus health);

    /** Upper bound for safe thrust / throttle under this state. */
    default double maxSafeThrottle() { return 100.0; }

    /** Whether manual operator override is authorized under this state. */
    default boolean allowsManualOverride() { return true; }

    /** Whether the spacecraft is in protective safe mode lockdown. */
    default boolean isSafeMode() { return false; }

    /** Whether the spacecraft is operating under nominal flight conditions. */
    default boolean isNominal() { return false; }

    /** Human-readable status description for flight logs and telemetry displays. */
    default String description() { return "Operating state: " + name(); }

    /** Appropriate theme-driven visual indicator color for this state. */
    default Color badgeColor(Theme theme) { return theme.accent(); }
}

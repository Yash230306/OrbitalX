package com.orbitalix.gui;

import com.orbitalix.api.SimulationController;
import com.orbitalix.gui.theme.Theme;

/** Interactive dashboard canvas for telemetry and onboard operator commands. */
public final class DashboardView extends MissionView {
    public DashboardView(SimulationController controller, Theme theme) {
        super("Dashboard", controller, theme);
    }
}

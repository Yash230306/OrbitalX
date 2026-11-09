package com.orbitalix.model;

import com.orbitalix.api.HealthStatus;
import java.util.List;

/** Composite node shared by spacecraft, subsystems, and sensor leaves. */
public interface SystemComponent {
    String name();
    HealthStatus health();
    List<SystemComponent> children();

    default double healthScore() {
        return switch (health()) {
            case NOMINAL -> 1.0;
            case WARNING -> 0.65;
            case CRITICAL -> 0.25;
            case FAILED -> 0.0;
        };
    }

    static HealthStatus worstOf(List<? extends SystemComponent> components) {
        HealthStatus worst = HealthStatus.NOMINAL;
        for (SystemComponent component : components) {
            if (component.health().ordinal() > worst.ordinal()) worst = component.health();
        }
        return worst;
    }
}

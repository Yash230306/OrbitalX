package com.orbitalix.state;

import com.orbitalix.api.Copyable;
import com.orbitalix.api.HealthStatus;

public interface SpacecraftState extends Copyable<SpacecraftState> {
    String name();
    SpacecraftState transition(HealthStatus health);
}

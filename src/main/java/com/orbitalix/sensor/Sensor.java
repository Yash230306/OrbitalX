package com.orbitalix.sensor;

import com.orbitalix.api.Copyable;
import com.orbitalix.model.SystemComponent;
import com.orbitalix.util.Identifiable;
import java.util.List;

/** Sensor contract with immutable read snapshots and bounded measurement history. */
public interface Sensor extends Identifiable, Copyable<Sensor>, SystemComponent {
    String unit();
    double measure();
    Reading read();
    List<Reading> history();

    default double latest() {
        List<Reading> readings = history();
        return readings.isEmpty() ? measure() : readings.get(readings.size() - 1).value();
    }
}

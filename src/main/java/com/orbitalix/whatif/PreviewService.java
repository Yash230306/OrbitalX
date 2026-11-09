package com.orbitalix.whatif;

import com.orbitalix.api.PreviewResult;
import com.orbitalix.simulation.SimulationEngine;
import java.util.Objects;

/** What-if command forks the deterministic engine so forecasts never mutate live state. */
public final class PreviewService {
    public PreviewResult preview(SimulationEngine engine, String recommendationId, int lookaheadTicks) {
        return Objects.requireNonNull(engine).runForecast(recommendationId, lookaheadTicks);
    }
}

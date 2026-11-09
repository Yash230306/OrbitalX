package com.orbitalix.advisor;

import com.orbitalix.detection.Anomaly;
import com.orbitalix.model.Spacecraft;
import java.util.List;
import java.util.Optional;

public interface RecommendationRule {
    String id();
    Optional<Recommendation> evaluate(Spacecraft spacecraft, List<Anomaly> anomalies, int tick);
}

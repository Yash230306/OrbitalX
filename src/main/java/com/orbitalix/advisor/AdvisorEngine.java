package com.orbitalix.advisor;

import com.orbitalix.detection.Anomaly;
import com.orbitalix.model.Spacecraft;
import java.util.*;

/** Aggregates independent rules and returns stable priority-ordered advice. */
public final class AdvisorEngine {
    private final List<RecommendationRule> rules;
    public AdvisorEngine() {
        this(List.of(new FuelLowRule(), new OverheatingRule(), new CoolantFailureRule(), new PowerShortageRule(),
                new SolarFailureRule(), new CommunicationBlackoutRule(), new GyroDriftRule(), new LifeSupportRule(),
                new HullBreachRule(), new RadiationStormRule(), new NavigationDriftRule(), new DebrisThreatRule(),
                new ComputerFailureRule(), new EngineMisfireRule(), new LimitOverrideRule()));
    }
    public AdvisorEngine(List<? extends RecommendationRule> rules) {
        if (rules == null || rules.isEmpty()) throw new IllegalArgumentException("At least one recommendation rule is required");
        this.rules = List.copyOf(rules);
    }
    public List<RecommendationRule> rules() { return rules; }
    public List<Recommendation> advise(Spacecraft spacecraft, List<Anomaly> anomalies, int tick) {
        List<Recommendation> recommendations = new ArrayList<>();
        for (RecommendationRule rule : rules)
            rule.evaluate(spacecraft, anomalies, tick).ifPresent(recommendations::add);
        Collections.sort(recommendations);
        return List.copyOf(recommendations);
    }
}

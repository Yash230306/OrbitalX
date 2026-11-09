package com.orbitalix.advisor;

import com.orbitalix.action.CorrectiveAction;
import com.orbitalix.detection.Anomaly;
import com.orbitalix.model.Spacecraft;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class ConditionRule implements RecommendationRule {
    private final String id;
    private final String title;
    private final String explanation;
    private final int priority;
    private final Predicate<Spacecraft> condition;
    private final Supplier<CorrectiveAction> action;
    ConditionRule(String id, String title, String explanation, int priority,
                  Predicate<Spacecraft> condition, Supplier<CorrectiveAction> action) {
        this.id = id; this.title = title; this.explanation = explanation; this.priority = priority;
        this.condition = condition; this.action = action;
    }
    @Override public final String id() { return id; }
    @Override public final Optional<Recommendation> evaluate(Spacecraft craft, List<Anomaly> anomalies, int tick) {
        if (!condition.test(craft)) return Optional.empty();
        return Optional.of(new Recommendation(id, title, explanation, priority, 5, action.get()));
    }
}

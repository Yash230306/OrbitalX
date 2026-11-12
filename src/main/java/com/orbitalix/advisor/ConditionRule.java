package com.orbitalix.advisor;

import com.orbitalix.action.CorrectiveAction;
import com.orbitalix.api.CrewTaskType;
import com.orbitalix.detection.Anomaly;
import com.orbitalix.model.Spacecraft;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Reusable rule base that fires when a spacecraft predicate is satisfied.
 *
 * <p>OOP note: the optional {@link CrewTaskType} parameter lets each concrete
 * rule declare — at construction time — whether crew assistance is needed.
 * This keeps the knowledge inside the rule hierarchy where it belongs and
 * removes the string-switch from {@code CrewHandler}.</p>
 */
abstract class ConditionRule implements RecommendationRule {
    private final String id;
    private final String title;
    private final String explanation;
    private final int priority;
    private final Predicate<Spacecraft> condition;
    private final Supplier<CorrectiveAction> action;
    private final CrewTaskType requiredCrewTask;   // null = no crew needed

    /** Convenience constructor for rules that do not require crew. */
    ConditionRule(String id, String title, String explanation, int priority,
                  Predicate<Spacecraft> condition, Supplier<CorrectiveAction> action) {
        this(id, title, explanation, priority, condition, action, null);
    }

    /** Full constructor that declares a required crew task alongside the corrective action. */
    ConditionRule(String id, String title, String explanation, int priority,
                  Predicate<Spacecraft> condition, Supplier<CorrectiveAction> action,
                  CrewTaskType requiredCrewTask) {
        this.id = id; this.title = title; this.explanation = explanation; this.priority = priority;
        this.condition = condition; this.action = action;
        this.requiredCrewTask = requiredCrewTask;
    }

    @Override public final String id() { return id; }

    @Override public final Optional<Recommendation> evaluate(Spacecraft craft, List<Anomaly> anomalies, int tick) {
        if (!condition.test(craft)) return Optional.empty();
        return Optional.of(new Recommendation(id, title, explanation, priority, 5, action.get(), requiredCrewTask));
    }
}

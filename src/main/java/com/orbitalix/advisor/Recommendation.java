package com.orbitalix.advisor;

import com.orbitalix.action.CorrectiveAction;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.api.CrewTaskType;
import java.util.Objects;
import java.util.Optional;

/**
 * Comparable advice item with stable identity, priority, executable command, and
 * an optional crew-task declaration.
 *
 * <p>OOP note: encapsulating the required {@link CrewTaskType} here (rather than
 * hard-coding recommendation IDs inside the handler) upholds the
 * <em>Open/Closed Principle</em> — the {@code CrewHandler} no longer needs to
 * be modified when a new crew-assisted recommendation is introduced.</p>
 */
public final class Recommendation implements Comparable<Recommendation> {
    private final String id;
    private final String title;
    private final String explanation;
    private final int priority;
    private final int ticksLeft;
    private final CorrectiveAction action;
    /** Non-null when crew assistance is needed to carry out this recommendation. */
    private final CrewTaskType requiredCrewTask;

    /** Constructor for recommendations that do not require crew assistance. */
    public Recommendation(String id, String title, String explanation, int priority, int ticksLeft, CorrectiveAction action) {
        this(id, title, explanation, priority, ticksLeft, action, null);
    }

    /** Constructor for recommendations that require a specific crew task. */
    public Recommendation(String id, String title, String explanation, int priority, int ticksLeft,
                          CorrectiveAction action, CrewTaskType requiredCrewTask) {
        if (id == null || id.isBlank() || title == null || title.isBlank() || explanation == null || ticksLeft < 0)
            throw new IllegalArgumentException("Invalid recommendation");
        this.id = id;
        this.title = title;
        this.explanation = explanation;
        this.priority = priority;
        this.ticksLeft = ticksLeft;
        this.action = Objects.requireNonNull(action);
        this.requiredCrewTask = requiredCrewTask;   // null means no crew needed
    }

    public String id() { return id; }
    public String title() { return title; }
    public String explanation() { return explanation; }
    public int priority() { return priority; }
    public int ticksLeft() { return ticksLeft; }
    public CorrectiveAction action() { return action; }
    public ActionRisk risk() { return action.risk(); }

    /**
     * Returns the crew task required to handle this recommendation, if any.
     * An empty Optional means no crew member needs to be assigned.
     *
     * <p>OOP: this moves the "does this need crew?" knowledge into the
     * recommendation itself — where it belongs — instead of a switch-on-ID
     * in the handler (which would violate Open/Closed).</p>
     */
    public Optional<CrewTaskType> requiredCrewTask() { return Optional.ofNullable(requiredCrewTask); }

    @Override public int compareTo(Recommendation other) {
        int byPriority = Integer.compare(other.priority, priority);
        if (byPriority != 0) return byPriority;
        int byCountdown = Integer.compare(ticksLeft, other.ticksLeft);
        return byCountdown != 0 ? byCountdown : id.compareTo(other.id);
    }
    @Override public boolean equals(Object other) {
        return other instanceof Recommendation recommendation && id.equals(recommendation.id);
    }
    @Override public int hashCode() { return id.hashCode(); }
    @Override public String toString() { return "Recommendation[" + id + ", priority=" + priority + "]"; }
}

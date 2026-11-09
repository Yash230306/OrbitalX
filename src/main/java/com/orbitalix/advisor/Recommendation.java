package com.orbitalix.advisor;

import com.orbitalix.action.CorrectiveAction;
import com.orbitalix.api.ActionRisk;
import java.util.Objects;

/** Comparable advice item with stable identity, priority, and its executable command. */
public final class Recommendation implements Comparable<Recommendation> {
    private final String id;
    private final String title;
    private final String explanation;
    private final int priority;
    private final int ticksLeft;
    private final CorrectiveAction action;
    public Recommendation(String id, String title, String explanation, int priority, int ticksLeft, CorrectiveAction action) {
        if (id == null || id.isBlank() || title == null || title.isBlank() || explanation == null || ticksLeft < 0)
            throw new IllegalArgumentException("Invalid recommendation");
        this.id = id;
        this.title = title;
        this.explanation = explanation;
        this.priority = priority;
        this.ticksLeft = ticksLeft;
        this.action = Objects.requireNonNull(action);
    }
    public String id() { return id; }
    public String title() { return title; }
    public String explanation() { return explanation; }
    public int priority() { return priority; }
    public int ticksLeft() { return ticksLeft; }
    public CorrectiveAction action() { return action; }
    public ActionRisk risk() { return action.risk(); }
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

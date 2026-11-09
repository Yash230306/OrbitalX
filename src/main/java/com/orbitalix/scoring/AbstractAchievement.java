package com.orbitalix.scoring;

import java.util.function.Predicate;

abstract class AbstractAchievement implements Achievement {
    private final String id;
    private final String title;
    private final Predicate<MissionMetrics> condition;
    AbstractAchievement(String id, String title, Predicate<MissionMetrics> condition) {
        this.id = id; this.title = title; this.condition = condition;
    }
    @Override public final String id() { return id; }
    @Override public final String title() { return title; }
    @Override public final boolean earned(MissionMetrics metrics) { return condition.test(metrics); }
}

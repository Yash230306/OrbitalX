package com.orbitalix.scoring;
public final class CascadeSurvivorAchievement extends AbstractAchievement {
    public CascadeSurvivorAchievement() { super("cascade-survivor", "Cascade Survivor", metrics -> metrics.success() && metrics.cascadeScenario()); }
}

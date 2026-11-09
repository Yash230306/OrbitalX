package com.orbitalix.scoring;
public final class CowboyAchievement extends AbstractAchievement {
    public CowboyAchievement() { super("cowboy", "Cowboy", metrics -> metrics.success() && metrics.limitOverride()); }
}

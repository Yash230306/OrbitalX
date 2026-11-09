package com.orbitalix.scoring;
public final class DodgeThisAchievement extends AbstractAchievement {
    public DodgeThisAchievement() { super("dodge-this", "Dodge This", metrics -> metrics.debrisDodges() >= 3); }
}

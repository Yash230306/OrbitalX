package com.orbitalix.scoring;
public final class HandsOffAchievement extends AbstractAchievement {
    public HandsOffAchievement() { super("hands-off", "Hands-Off", metrics -> metrics.success() && metrics.fullAuto() && metrics.humanActions() == 0); }
}

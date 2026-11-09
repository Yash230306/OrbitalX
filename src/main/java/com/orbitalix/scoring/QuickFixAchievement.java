package com.orbitalix.scoring;
public final class QuickFixAchievement extends AbstractAchievement {
    public QuickFixAchievement() { super("quick-fix", "Quick Fix", metrics -> metrics.quickRecovery()); }
}

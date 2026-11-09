package com.orbitalix.scoring;
public final class PhoenixAchievement extends AbstractAchievement {
    public PhoenixAchievement() { super("phoenix", "Phoenix", metrics -> metrics.success() && metrics.recoveredFromCritical()); }
}

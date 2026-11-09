package com.orbitalix.scoring;
public final class GuardianAngelAchievement extends AbstractAchievement {
    public GuardianAngelAchievement() { super("guardian-angel", "Guardian Angel", metrics -> metrics.success() && !metrics.crewInjured()); }
}

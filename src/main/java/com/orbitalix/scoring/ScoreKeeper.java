package com.orbitalix.scoring;

import java.util.List;

public final class ScoreKeeper {
    private final List<Achievement> achievements = List.of(new IceColdAchievement(), new FuelMiserAchievement(),
            new HandsOffAchievement(), new PhoenixAchievement(), new DodgeThisAchievement(),
            new CascadeSurvivorAchievement(), new CowboyAchievement(), new GuardianAngelAchievement(),
            new QuickFixAchievement(), new WhatifWizardAchievement());
    public List<Achievement> achievements() { return achievements; }
    public List<Achievement> earned(MissionMetrics metrics) {
        return achievements.stream().filter(achievement -> achievement.earned(metrics)).toList();
    }
    public Grade grade(double score) {
        for (Grade grade : Grade.values()) if (score >= grade.minimumScore()) return grade;
        return Grade.F;
    }
    public double calculate(boolean success, double progress, double fuel, double battery,
                            double oxygen, double averageCrewHealth) {
        return (success ? 500 : 0) + progress * 2 + fuel * 2 + battery + oxygen + averageCrewHealth;
    }
}

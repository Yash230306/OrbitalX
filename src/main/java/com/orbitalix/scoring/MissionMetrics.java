package com.orbitalix.scoring;

public record MissionMetrics(boolean success, boolean fullAuto, int humanActions, boolean limitOverride,
        boolean recoveredFromCritical, int debrisDodges, boolean cascadeScenario, double fuelPercent,
        double crewHealthPercent, boolean crewInjured, double maximumTemperature, int previewCount,
        boolean quickRecovery) {}

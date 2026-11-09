package com.orbitalix.scoring;
public final class FuelMiserAchievement extends AbstractAchievement {
    public FuelMiserAchievement() { super("fuel-miser", "Fuel Miser", metrics -> metrics.success() && metrics.fuelPercent() > 40); }
}

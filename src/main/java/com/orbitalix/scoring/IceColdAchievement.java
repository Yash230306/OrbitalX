package com.orbitalix.scoring;
public final class IceColdAchievement extends AbstractAchievement {
    public IceColdAchievement() { super("ice-cold", "Ice Cold", metrics -> metrics.maximumTemperature() <= 85); }
}

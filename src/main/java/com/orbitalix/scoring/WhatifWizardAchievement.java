package com.orbitalix.scoring;
public final class WhatifWizardAchievement extends AbstractAchievement {
    public WhatifWizardAchievement() { super("what-if-wizard", "Whatif Wizard", metrics -> metrics.previewCount() >= 5); }
}

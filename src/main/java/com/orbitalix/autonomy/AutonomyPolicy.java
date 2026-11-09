package com.orbitalix.autonomy;

import com.orbitalix.advisor.Recommendation;

public interface AutonomyPolicy {
    boolean shouldAct(Recommendation recommendation, int waitingTicks, int responseWindow);
    String name();
}

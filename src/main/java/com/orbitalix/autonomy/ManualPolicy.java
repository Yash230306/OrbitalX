package com.orbitalix.autonomy;
import com.orbitalix.advisor.Recommendation;
public final class ManualPolicy implements AutonomyPolicy {
    @Override public boolean shouldAct(Recommendation recommendation, int waitingTicks, int responseWindow) { return false; }
    @Override public String name() { return "MANUAL"; }
}

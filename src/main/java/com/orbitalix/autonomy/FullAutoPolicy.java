package com.orbitalix.autonomy;
import com.orbitalix.advisor.Recommendation;
public final class FullAutoPolicy implements AutonomyPolicy {
    @Override public boolean shouldAct(Recommendation recommendation, int waitingTicks, int responseWindow) { return true; }
    @Override public String name() { return "FULL_AUTO"; }
}

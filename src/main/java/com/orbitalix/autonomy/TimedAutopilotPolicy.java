package com.orbitalix.autonomy;
import com.orbitalix.advisor.Recommendation;
public final class TimedAutopilotPolicy implements AutonomyPolicy {
    @Override public boolean shouldAct(Recommendation recommendation, int waitingTicks, int responseWindow) {
        return waitingTicks >= Math.max(1, responseWindow);
    }
    @Override public String name() { return "TIMED_AUTOPILOT"; }
}

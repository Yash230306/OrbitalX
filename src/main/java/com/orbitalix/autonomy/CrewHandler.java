package com.orbitalix.autonomy;

import com.orbitalix.action.ActionResult;
import com.orbitalix.api.*;
import com.orbitalix.crew.CrewMember;
import com.orbitalix.model.Spacecraft;
import java.util.List;

public final class CrewHandler extends ResponseHandler {
    @Override protected ResponseDecision tryHandle(ResponseContext context) {
        if (!context.computerOnline() || context.policy() instanceof ManualPolicy
                || !context.policy().shouldAct(context.recommendation(), context.waitingTicks(), context.responseWindow())) return null;
        String member = available(context.spacecraft().crewRoster().members());
        if (member == null) return null;
        CrewTaskType task;
        FaultType fault = null;
        switch (context.recommendation().id()) {
            case "hull-breach" -> { task = CrewTaskType.EVA_REPAIR; fault = FaultType.HULL_BREACH; }
            case "computer-failure" -> task = CrewTaskType.MANUAL_REBOOT;
            case "life-support" -> task = CrewTaskType.MEDICAL_CARE;
            default -> { return null; }
        }
        if (task == CrewTaskType.EVA_REPAIR && !context.evaAllowed()) return null;
        context.spacecraft().crewRoster().assign(member, task, fault, context.evaAllowed());
        return new ResponseDecision(true, "CREW", ActionResult.success(member + " assigned " + task), false, context.escalated());
    }
    private static String available(List<CrewMember> members) {
        return members.stream().filter(CrewMember::alive).filter(member -> member.task() == null)
                .map(CrewMember::name).findFirst().orElse(null);
    }
}

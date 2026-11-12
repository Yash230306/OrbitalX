package com.orbitalix.autonomy;

import com.orbitalix.action.ActionResult;
import com.orbitalix.api.CrewTaskType;
import com.orbitalix.api.FaultType;
import com.orbitalix.crew.CrewMember;
import com.orbitalix.model.Spacecraft;
import java.util.List;
import java.util.Optional;

/**
 * Chain-of-Responsibility handler that assigns an available crew member when the
 * active {@link com.orbitalix.advisor.Recommendation} declares a required crew task.
 *
 * <p><b>OOP fix (Open/Closed Principle):</b> the previous implementation contained a
 * hard-coded {@code switch} on recommendation ID strings. Any new recommendation that
 * required crew forced an edit here — a direct violation of the Open/Closed Principle.
 *
 * <p>The fix moves the crew-task declaration into {@link com.orbitalix.advisor.Recommendation}
 * via {@code requiredCrewTask()}.  This handler now asks the recommendation itself whether
 * crew is needed.  Adding a new crew-assisted recommendation means only writing or updating
 * the rule class — this handler requires zero modifications.</p>
 */
public final class CrewHandler extends ResponseHandler {

    @Override protected ResponseDecision tryHandle(ResponseContext context) {
        // Skip if manual mode or policy says it is not time to act.
        if (!context.computerOnline() || context.policy() instanceof ManualPolicy
                || !context.policy().shouldAct(context.recommendation(), context.waitingTicks(), context.responseWindow()))
            return null;

        // Polymorphic dispatch: the recommendation declares its own crew-task requirement.
        Optional<CrewTaskType> maybeTask = context.recommendation().requiredCrewTask();
        if (maybeTask.isEmpty()) return null;           // this recommendation needs no crew
        CrewTaskType task = maybeTask.get();

        // EVA is only permitted under safe environmental conditions.
        if (task == CrewTaskType.EVA_REPAIR && !context.evaAllowed()) return null;

        String member = available(context.spacecraft().crewRoster().members());
        if (member == null) return null;               // all crew are busy or incapacitated

        // Determine whether the fault should be cleared on completion (EVA repair only).
        FaultType clearedFault = (task == CrewTaskType.EVA_REPAIR) ? FaultType.HULL_BREACH : null;
        context.spacecraft().crewRoster().assign(member, task, clearedFault, context.evaAllowed());

        return new ResponseDecision(true, "CREW",
                ActionResult.success(member + " assigned " + task), false, context.escalated());
    }

    private static String available(List<CrewMember> members) {
        return members.stream()
                .filter(CrewMember::alive)
                .filter(m -> m.task() == null)
                .map(CrewMember::name)
                .findFirst()
                .orElse(null);
    }
}


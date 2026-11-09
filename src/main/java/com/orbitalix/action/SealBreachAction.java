package com.orbitalix.action;
import com.orbitalix.api.ActionRisk;
import com.orbitalix.model.Spacecraft;
public final class SealBreachAction extends AbstractCorrectiveAction {
    public SealBreachAction() { super(idFor(SealBreachAction.class), "Seal hull breach", ActionRisk.COSTLY); }
    @Override public ActionResult execute(Spacecraft craft) {
        return craft.sealBreach() ? success("Hull breach sealed") : failure("No sealant remains");
    }
}

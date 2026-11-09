package com.orbitalix.advisor;
import com.orbitalix.action.*;
public final class LimitOverrideRule extends ConditionRule {
    public LimitOverrideRule() { super("limit-override", "Unsafe limit override", "One or more operating limits exceed design values.", 82,
            craft -> craft.limits().overridden(), RestoreSafeLimitsAction::new); }
}

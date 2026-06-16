package com.company.scopery.modules.aiagent.usagepolicy.application.evaluator;

import java.util.List;

public record UsagePolicyEvaluationResult(
        UsagePolicyDecision decision,
        List<UsagePolicyViolation> violations,
        List<UsagePolicyViolation> warnings
) {
    public boolean isBlocked() { return decision == UsagePolicyDecision.BLOCKED; }
    public boolean isWarn()    { return decision == UsagePolicyDecision.WARN; }
    public boolean isAllowed() { return decision == UsagePolicyDecision.ALLOWED; }
}

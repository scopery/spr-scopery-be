package com.company.scopery.modules.iam.ownerpolicy.application.response;

import com.company.scopery.modules.iam.ownerpolicy.domain.model.IamOwnerPolicy;
import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record IamOwnerPolicyResponse(
        UUID id, String resourceType, int policyVersion, String status,
        List<IamOwnerPolicyAction> actions, String inheritanceScope,
        boolean canDelegate, int delegationDepth, Instant effectiveFrom,
        Instant effectiveTo, int version) {
    public static IamOwnerPolicyResponse from(IamOwnerPolicy policy) {
        return new IamOwnerPolicyResponse(policy.id(), policy.resourceType().name(), policy.policyVersion(),
                policy.status().name(), policy.actionBundle(), policy.inheritanceScope().name(),
                policy.canDelegate(), policy.delegationDepth(), policy.effectiveFrom(),
                policy.effectiveTo(), policy.version());
    }
}

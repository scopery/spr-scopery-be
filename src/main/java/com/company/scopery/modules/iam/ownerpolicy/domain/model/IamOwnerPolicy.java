package com.company.scopery.modules.iam.ownerpolicy.domain.model;

import com.company.scopery.modules.iam.ownerpolicy.domain.enums.IamInheritanceScope;
import com.company.scopery.modules.iam.ownerpolicy.domain.enums.IamOwnerPolicyStatus;
import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record IamOwnerPolicy(
        UUID id,
        IamResourceType resourceType,
        int policyVersion,
        IamOwnerPolicyStatus status,
        List<IamOwnerPolicyAction> actionBundle,
        IamInheritanceScope inheritanceScope,
        boolean canDelegate,
        int delegationDepth,
        Instant effectiveFrom,
        Instant effectiveTo,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static IamOwnerPolicy create(IamResourceType resourceType, int policyVersion,
                                        List<IamOwnerPolicyAction> actions,
                                        IamInheritanceScope inheritanceScope,
                                        boolean canDelegate, int delegationDepth) {
        if (actions == null || actions.isEmpty()) throw new IllegalArgumentException("Owner policy actions are required");
        if (delegationDepth < 0) throw new IllegalArgumentException("Delegation depth must not be negative");
        Instant now = Instant.now();
        return new IamOwnerPolicy(UUID.randomUUID(), resourceType, policyVersion,
                IamOwnerPolicyStatus.ACTIVE, List.copyOf(actions), inheritanceScope,
                canDelegate, delegationDepth, now, null, 0, null, null);
    }

    public IamOwnerPolicy supersede() {
        Instant now = Instant.now();
        return new IamOwnerPolicy(id, resourceType, policyVersion, IamOwnerPolicyStatus.SUPERSEDED,
                actionBundle, inheritanceScope, canDelegate, delegationDepth,
                effectiveFrom, now, version, createdAt, now);
    }
}

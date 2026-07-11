package com.company.scopery.modules.iam.grant.application.command;

import com.company.scopery.modules.iam.ownerpolicy.domain.valueobject.IamOwnerPolicyAction;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DelegateIamAccessCommand(
        String subjectType,
        UUID subjectId,
        String resourceType,
        UUID resourceRefId,
        int delegationDepth,
        Instant expiresAt,
        String conditionJson,
        String reason,
        List<IamOwnerPolicyAction> actions) {
}

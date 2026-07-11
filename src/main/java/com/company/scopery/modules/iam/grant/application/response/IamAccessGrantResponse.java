package com.company.scopery.modules.iam.grant.application.response;

import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;

import java.time.Instant;
import java.util.UUID;

public record IamAccessGrantResponse(
        UUID id,
        String subjectType,
        UUID subjectId,
        UUID resourceId,
        UUID roleId,
        String effect,
        String scopeType,
        UUID scopeRefId,
        UUID workspaceId,
        String kind,
        UUID sourcePolicyId,
        boolean canDelegate,
        int delegationDepth,
        Instant expiresAt,
        String conditionJson,
        String reason,
        String status,
        UUID grantedBy,
        Instant grantedAt,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static IamAccessGrantResponse from(IamAccessGrant domain) {
        return new IamAccessGrantResponse(
                domain.id(),
                domain.subjectType().name(),
                domain.subjectId(),
                domain.resourceId(),
                domain.roleId(),
                domain.effect().name(),
                domain.scopeType() != null ? domain.scopeType().name() : null,
                domain.scopeRefId(),
                domain.workspaceId(),
                domain.kind().name(),
                domain.sourcePolicyId(),
                domain.canDelegate(),
                domain.delegationDepth(),
                domain.expiresAt(),
                domain.conditionJson(),
                domain.reason(),
                domain.status().name(),
                domain.grantedBy(),
                domain.grantedAt(),
                domain.version(),
                domain.createdAt(),
                domain.updatedAt()
        );
    }
}

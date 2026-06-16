package com.company.scopery.modules.iam.grant.application.response;

import com.company.scopery.modules.iam.grant.domain.IamAccessGrant;

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
        String status,
        UUID grantedBy,
        Instant grantedAt,
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
                domain.status().name(),
                domain.grantedBy(),
                domain.grantedAt(),
                domain.createdAt(),
                domain.updatedAt()
        );
    }
}

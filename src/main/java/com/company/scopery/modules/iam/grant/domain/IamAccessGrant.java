package com.company.scopery.modules.iam.grant.domain;

import java.time.Instant;
import java.util.UUID;

public record IamAccessGrant(
        UUID id,
        IamSubjectType subjectType,
        UUID subjectId,
        UUID resourceId,
        UUID roleId,
        IamGrantEffect effect,
        IamGrantScopeType scopeType,
        UUID scopeRefId,
        UUID workspaceId,
        IamAccessGrantStatus status,
        UUID grantedBy,
        Instant grantedAt,
        Instant createdAt,
        Instant updatedAt) {

    public static IamAccessGrant create(IamSubjectType subjectType, UUID subjectId,
                                         UUID resourceId, UUID roleId,
                                         IamGrantEffect effect,
                                         IamGrantScopeType scopeType, UUID scopeRefId,
                                         UUID workspaceId, UUID grantedBy) {
        Instant now = Instant.now();
        return new IamAccessGrant(UUID.randomUUID(), subjectType, subjectId, resourceId, roleId,
                effect == null ? IamGrantEffect.ALLOW : effect,
                scopeType, scopeRefId, workspaceId,
                IamAccessGrantStatus.ACTIVE, grantedBy, now, now, now);
    }

    public IamAccessGrant revoke() {
        return new IamAccessGrant(id, subjectType, subjectId, resourceId, roleId,
                effect, scopeType, scopeRefId, workspaceId,
                IamAccessGrantStatus.REVOKED, grantedBy, grantedAt, createdAt, Instant.now());
    }
}

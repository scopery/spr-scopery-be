package com.company.scopery.modules.iam.grant.domain.model;

import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantScopeType;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantKind;

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
        IamGrantKind kind,
        UUID sourcePolicyId,
        boolean canDelegate,
        int delegationDepth,
        Instant expiresAt,
        String conditionJson,
        String reason,
        IamAccessGrantStatus status,
        UUID grantedBy,
        Instant grantedAt,
        int version,
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
                defaultKind(subjectType), null, false, 0, null, null, null,
                IamAccessGrantStatus.ACTIVE, grantedBy, now, 0, null, null);
    }

    public static IamAccessGrant createWithMetadata(
            IamSubjectType subjectType, UUID subjectId, UUID resourceId, UUID roleId,
            IamGrantEffect effect, IamGrantScopeType scopeType, UUID scopeRefId, UUID workspaceId,
            IamGrantKind kind, UUID sourcePolicyId, boolean canDelegate, int delegationDepth,
            Instant expiresAt, String conditionJson, String reason, UUID grantedBy) {
        if (delegationDepth < 0) throw new IllegalArgumentException("Delegation depth must not be negative");
        Instant now = Instant.now();
        return new IamAccessGrant(UUID.randomUUID(), subjectType, subjectId, resourceId, roleId,
                effect == null ? IamGrantEffect.ALLOW : effect, scopeType, scopeRefId, workspaceId,
                kind, sourcePolicyId, canDelegate, delegationDepth, expiresAt, conditionJson, reason,
                IamAccessGrantStatus.ACTIVE, grantedBy, now, 0, null, null);
    }

    public IamAccessGrant withScopeType(IamGrantScopeType newScopeType) {
        return new IamAccessGrant(id, subjectType, subjectId, resourceId, roleId,
                effect, newScopeType, scopeRefId, workspaceId,
                kind, sourcePolicyId, canDelegate, delegationDepth, expiresAt, conditionJson, reason,
                status, grantedBy, grantedAt, version, createdAt, Instant.now());
    }

    public IamAccessGrant revoke() {
        return new IamAccessGrant(id, subjectType, subjectId, resourceId, roleId,
                effect, scopeType, scopeRefId, workspaceId,
                kind, sourcePolicyId, canDelegate, delegationDepth, expiresAt, conditionJson, reason,
                IamAccessGrantStatus.REVOKED, grantedBy, grantedAt, version, createdAt, Instant.now());
    }

    public boolean isEffectiveAt(Instant instant) {
        return status == IamAccessGrantStatus.ACTIVE && (expiresAt == null || expiresAt.isAfter(instant));
    }

    private static IamGrantKind defaultKind(IamSubjectType subjectType) {
        return switch (subjectType) {
            case USER -> IamGrantKind.DIRECT;
            case TEAM -> IamGrantKind.TEAM;
            case ROLE -> IamGrantKind.ROLE;
        };
    }
}

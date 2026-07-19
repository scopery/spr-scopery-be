package com.company.scopery.modules.ratecard.costrole.domain.model;

import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleScope;
import com.company.scopery.modules.ratecard.costrole.domain.enums.CostRoleStatus;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

public record CostRole(
        UUID id,
        String code,
        String name,
        String description,
        CostRoleScope scope,
        UUID organizationId,
        UUID workspaceId,
        String category,
        boolean builtIn,
        CostRoleStatus status,
        Instant archivedAt,
        UUID archivedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static String normalizeCode(String code) {
        if (code == null) {
            return null;
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }

    public static CostRole create(String code, String name, String description, CostRoleScope scope,
                                  UUID organizationId, UUID workspaceId, String category, boolean builtIn) {
        return new CostRole(
                UUID.randomUUID(),
                normalizeCode(code),
                name,
                description,
                scope,
                organizationId,
                workspaceId,
                category,
                builtIn,
                CostRoleStatus.ACTIVE,
                null, null, 0, null, null
        );
    }

    public static CostRole createSystemBuiltIn(String code, String name, String category) {
        return create(code, name, null, CostRoleScope.SYSTEM, null, null, category, true);
    }

    public CostRole update(String name, String description, String category) {
        return new CostRole(
                id, code, name, description, scope, organizationId, workspaceId, category,
                builtIn, status, archivedAt, archivedBy, version, createdAt, updatedAt
        );
    }

    public CostRole activate() {
        return withStatus(CostRoleStatus.ACTIVE, null, null);
    }

    public CostRole deactivate() {
        return withStatus(CostRoleStatus.INACTIVE, archivedAt, archivedBy);
    }

    public CostRole archive(UUID actorId) {
        return withStatus(CostRoleStatus.ARCHIVED, Instant.now(), actorId);
    }

    private CostRole withStatus(CostRoleStatus newStatus, Instant archivedAt, UUID archivedBy) {
        return new CostRole(
                id, code, name, description, scope, organizationId, workspaceId, category,
                builtIn, newStatus, archivedAt, archivedBy, version, createdAt, updatedAt
        );
    }
}

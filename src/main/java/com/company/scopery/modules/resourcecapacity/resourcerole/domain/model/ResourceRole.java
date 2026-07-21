package com.company.scopery.modules.resourcecapacity.resourcerole.domain.model;
import com.company.scopery.modules.resourcecapacity.resourcerole.domain.enums.ResourceRoleStatus;
import java.time.Instant; import java.util.UUID;
public record ResourceRole(UUID id, UUID workspaceId, String roleCode, String name, String description,
        UUID defaultRateCardId, ResourceRoleStatus status, Instant archivedAt, UUID archivedBy,
        int version, Instant createdAt, Instant updatedAt) {
    public static ResourceRole create(UUID workspaceId, String code, String name, String description, UUID rateCardId) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() → persist() (not merge/optimistic-lock).
        return new ResourceRole(UUID.randomUUID(), workspaceId, code, name, description, rateCardId,
                ResourceRoleStatus.ACTIVE, null, null, 0, null, null);
    }
    public ResourceRole archive(UUID actor) {
        return new ResourceRole(id, workspaceId, roleCode, name, description, defaultRateCardId,
                ResourceRoleStatus.ARCHIVED, Instant.now(), actor, version, createdAt, Instant.now());
    }
}

package com.company.scopery.modules.traceability.application.domain.model;
import com.company.scopery.modules.traceability.application.domain.enums.RegistryApplicationStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryApplication(UUID id, UUID workspaceId, String code, String name, String description, RegistryApplicationStatus status,
                                  UUID ownerUserId, int version, Instant createdAt, Instant updatedAt) {
    public static RegistryApplication create(UUID workspaceId, String code, String name, String description, UUID ownerUserId) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() stays true on first persist.
        return new RegistryApplication(UUID.randomUUID(), workspaceId, code, name, description, RegistryApplicationStatus.ACTIVE, ownerUserId, 0, null, null);
    }
}

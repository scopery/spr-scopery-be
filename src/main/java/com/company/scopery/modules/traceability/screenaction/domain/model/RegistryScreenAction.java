package com.company.scopery.modules.traceability.screenaction.domain.model;
import com.company.scopery.modules.traceability.screenaction.domain.enums.RegistryScreenActionStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryScreenAction(UUID id, UUID screenId, UUID workspaceId, String actionCode, String name,
                                   String actionType, String description, int displayOrder,
                                   RegistryScreenActionStatus status, int version,
                                   Instant createdAt, Instant updatedAt) {
    public static RegistryScreenAction create(UUID screenId, UUID workspaceId, String actionCode, String name,
                                              String actionType, String description, int displayOrder) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() stays true on first persist.
        return new RegistryScreenAction(UUID.randomUUID(), screenId, workspaceId, actionCode, name, actionType,
                description, displayOrder, RegistryScreenActionStatus.ACTIVE, 0, null, null);
    }
    public RegistryScreenAction withUpdated(String name, String actionType, String description, int displayOrder) {
        return new RegistryScreenAction(id, screenId, workspaceId, actionCode, name, actionType, description, displayOrder, status, version, createdAt, Instant.now());
    }
}

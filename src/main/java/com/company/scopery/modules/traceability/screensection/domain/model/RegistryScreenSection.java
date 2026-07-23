package com.company.scopery.modules.traceability.screensection.domain.model;
import com.company.scopery.modules.traceability.screensection.domain.enums.RegistryScreenSectionStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryScreenSection(UUID id, UUID screenId, UUID workspaceId, String name, String description,
                                    int displayOrder, RegistryScreenSectionStatus status, int version,
                                    Instant createdAt, Instant updatedAt) {
    public static RegistryScreenSection create(UUID screenId, UUID workspaceId, String name, String description, int displayOrder) {
        // Leave createdAt/updatedAt null so AuditableJpaEntity.isNew() stays true on first persist.
        return new RegistryScreenSection(UUID.randomUUID(), screenId, workspaceId, name, description,
                displayOrder, RegistryScreenSectionStatus.ACTIVE, 0, null, null);
    }
    public RegistryScreenSection withUpdated(String name, String description, int displayOrder) {
        return new RegistryScreenSection(id, screenId, workspaceId, name, description, displayOrder, status, version, createdAt, Instant.now());
    }
}

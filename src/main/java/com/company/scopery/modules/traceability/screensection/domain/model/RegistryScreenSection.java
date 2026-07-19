package com.company.scopery.modules.traceability.screensection.domain.model;
import com.company.scopery.modules.traceability.screensection.domain.enums.RegistryScreenSectionStatus;
import java.time.Instant; import java.util.UUID;
public record RegistryScreenSection(UUID id, UUID screenId, UUID workspaceId, String name, String description,
                                    int displayOrder, RegistryScreenSectionStatus status, int version,
                                    Instant createdAt, Instant updatedAt) {
    public static RegistryScreenSection create(UUID screenId, UUID workspaceId, String name, String description, int displayOrder) {
        Instant now = Instant.now();
        return new RegistryScreenSection(UUID.randomUUID(), screenId, workspaceId, name, description,
                displayOrder, RegistryScreenSectionStatus.ACTIVE, 0, now, now);
    }
}

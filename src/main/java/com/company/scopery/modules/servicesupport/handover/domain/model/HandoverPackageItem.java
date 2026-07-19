package com.company.scopery.modules.servicesupport.handover.domain.model;
import java.time.Instant; import java.util.UUID;
public record HandoverPackageItem(UUID id, UUID workspaceId, UUID handoverPackageId, String itemType,
        String targetObjectType, UUID targetObjectId, UUID documentId, String title, String description,
        boolean clientVisible, int sortOrder, int version, Instant createdAt, Instant updatedAt) {
    public static HandoverPackageItem create(UUID workspaceId, UUID handoverPackageId, String itemType, String title) {
        Instant now = Instant.now();
        return new HandoverPackageItem(UUID.randomUUID(), workspaceId, handoverPackageId, itemType, null, null, null,
                title, null, false, 0, 0, now, now);
    }
}

package com.company.scopery.modules.servicesupport.handover.application.response;
import com.company.scopery.modules.servicesupport.handover.domain.model.HandoverPackageItem;
import java.time.Instant; import java.util.UUID;
public record HandoverPackageItemResponse(UUID id, UUID workspaceId, UUID handoverPackageId, String itemType,
        String title, boolean clientVisible, int sortOrder, Instant createdAt) {
    public static HandoverPackageItemResponse from(HandoverPackageItem d) {
        return new HandoverPackageItemResponse(d.id(), d.workspaceId(), d.handoverPackageId(), d.itemType(),
                d.title(), d.clientVisible(), d.sortOrder(), d.createdAt());
    }
}

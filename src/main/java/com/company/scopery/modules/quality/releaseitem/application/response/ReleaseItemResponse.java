package com.company.scopery.modules.quality.releaseitem.application.response;
import com.company.scopery.modules.quality.releaseitem.domain.model.ReleaseItem;
import java.time.Instant; import java.util.UUID;
public record ReleaseItemResponse(UUID id, UUID projectId, UUID releasePackageId, String targetType, UUID targetId, boolean required, String status, String notes, Instant createdAt) {
    public static ReleaseItemResponse from(ReleaseItem e) {
        return new ReleaseItemResponse(e.id(), e.projectId(), e.releasePackageId(), e.targetType(), e.targetId(), e.required(), e.status().name(), e.notes(), e.createdAt());
    }
}

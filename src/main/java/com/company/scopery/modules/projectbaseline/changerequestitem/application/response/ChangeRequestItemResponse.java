package com.company.scopery.modules.projectbaseline.changerequestitem.application.response;

import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItem;
import java.time.Instant; import java.util.UUID;

public record ChangeRequestItemResponse(
        UUID id, UUID changeRequestId, UUID projectId, String targetType, UUID targetId, String operation,
        String summary, String beforeSnapshotJson, String afterSnapshotJson, String applyPayloadJson,
        String status, Instant createdAt, Instant updatedAt
) {
    public static ChangeRequestItemResponse from(ChangeRequestItem i) {
        return new ChangeRequestItemResponse(i.id(), i.changeRequestId(), i.projectId(), i.targetType().name(),
                i.targetId(), i.operation().name(), i.summary(), i.beforeSnapshotJson(), i.afterSnapshotJson(),
                i.applyPayloadJson(), i.status().name(), i.createdAt(), i.updatedAt());
    }
}

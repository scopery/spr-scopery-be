package com.company.scopery.modules.projectbaseline.changerequestitem.domain.model;

import com.company.scopery.modules.projectbaseline.changerequestitem.domain.enums.ChangeItemOperation;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.enums.ChangeItemStatus;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.enums.ChangeItemTargetType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChangeRequestItem(
        UUID id,
        UUID changeRequestId,
        UUID projectId,
        ChangeItemTargetType targetType,
        UUID targetId,
        ChangeItemOperation operation,
        String summary,
        String beforeSnapshotJson,
        String afterSnapshotJson,
        String applyPayloadJson,
        ChangeItemStatus status,
        List<String> affectedAreas,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static ChangeRequestItem create(
            UUID changeRequestId, UUID projectId, ChangeItemTargetType targetType, UUID targetId,
            ChangeItemOperation operation, String summary, String beforeSnapshotJson,
            String afterSnapshotJson, String applyPayloadJson, List<String> affectedAreas) {
        return new ChangeRequestItem(
                UUID.randomUUID(), changeRequestId, projectId, targetType, targetId, operation, summary,
                beforeSnapshotJson, afterSnapshotJson, applyPayloadJson, ChangeItemStatus.DRAFT,
                affectedAreas, 0, null, null);
    }

    public static ChangeRequestItem create(
            UUID changeRequestId, UUID projectId, ChangeItemTargetType targetType, UUID targetId,
            ChangeItemOperation operation, String summary, String beforeSnapshotJson,
            String afterSnapshotJson, String applyPayloadJson) {
        return create(changeRequestId, projectId, targetType, targetId, operation, summary,
                beforeSnapshotJson, afterSnapshotJson, applyPayloadJson, null);
    }

    public ChangeRequestItem update(ChangeItemTargetType targetType, UUID targetId, ChangeItemOperation operation,
                                    String summary, String beforeSnapshotJson, String afterSnapshotJson,
                                    String applyPayloadJson, List<String> affectedAreas) {
        return new ChangeRequestItem(id, changeRequestId, projectId, targetType, targetId, operation, summary,
                beforeSnapshotJson, afterSnapshotJson, applyPayloadJson, status, affectedAreas,
                version, createdAt, updatedAt);
    }

    public ChangeRequestItem update(ChangeItemTargetType targetType, UUID targetId, ChangeItemOperation operation,
                                    String summary, String beforeSnapshotJson, String afterSnapshotJson,
                                    String applyPayloadJson) {
        return update(targetType, targetId, operation, summary, beforeSnapshotJson, afterSnapshotJson,
                applyPayloadJson, this.affectedAreas);
    }

    public ChangeRequestItem markApplied() {
        return new ChangeRequestItem(id, changeRequestId, projectId, targetType, targetId, operation, summary,
                beforeSnapshotJson, afterSnapshotJson, applyPayloadJson, ChangeItemStatus.APPLIED,
                affectedAreas, version, createdAt, updatedAt);
    }

    public ChangeRequestItem markFailed() {
        return new ChangeRequestItem(id, changeRequestId, projectId, targetType, targetId, operation, summary,
                beforeSnapshotJson, afterSnapshotJson, applyPayloadJson, ChangeItemStatus.FAILED,
                affectedAreas, version, createdAt, updatedAt);
    }
}

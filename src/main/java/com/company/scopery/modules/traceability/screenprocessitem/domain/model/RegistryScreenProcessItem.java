package com.company.scopery.modules.traceability.screenprocessitem.domain.model;

import com.company.scopery.modules.traceability.screenprocessitem.domain.enums.RegistryScreenProcessItemStatus;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenProcessItem(
        UUID id,
        UUID screenId,
        UUID workspaceId,
        UUID modeId,
        UUID targetFieldId,
        String title,
        String content,
        String sourceTable,
        String conditionNote,
        int displayOrder,
        RegistryScreenProcessItemStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static RegistryScreenProcessItem create(UUID screenId, UUID workspaceId, UUID modeId,
                                                   UUID targetFieldId, String title, String content,
                                                   String sourceTable, String conditionNote,
                                                   int displayOrder) {
        return new RegistryScreenProcessItem(
                UUID.randomUUID(), screenId, workspaceId, modeId, targetFieldId,
                title, content, sourceTable, conditionNote, displayOrder,
                RegistryScreenProcessItemStatus.ACTIVE, 0, null, null);
    }

    public RegistryScreenProcessItem withUpdated(UUID modeId, UUID targetFieldId, String title,
                                                 String content, String sourceTable,
                                                 String conditionNote, int displayOrder) {
        return new RegistryScreenProcessItem(
                id, screenId, workspaceId, modeId, targetFieldId,
                title, content, sourceTable, conditionNote, displayOrder,
                status, version, createdAt, Instant.now());
    }
}

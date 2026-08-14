package com.company.scopery.modules.traceability.screeneventitem.domain.model;

import com.company.scopery.modules.traceability.screeneventitem.domain.enums.RegistryScreenEventItemStatus;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenEventItem(
        UUID id,
        UUID screenId,
        UUID workspaceId,
        UUID modeId,
        UUID triggerFieldId,
        String triggerActionCode,
        String title,
        String content,
        String conditionNote,
        UUID targetScreenId,
        String targetModeCode,
        int displayOrder,
        RegistryScreenEventItemStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static RegistryScreenEventItem create(UUID screenId, UUID workspaceId, UUID modeId,
                                                 UUID triggerFieldId, String triggerActionCode,
                                                 String title, String content, String conditionNote,
                                                 UUID targetScreenId, String targetModeCode,
                                                 int displayOrder) {
        return new RegistryScreenEventItem(
                UUID.randomUUID(), screenId, workspaceId, modeId, triggerFieldId,
                triggerActionCode, title, content, conditionNote, targetScreenId, targetModeCode,
                displayOrder, RegistryScreenEventItemStatus.ACTIVE, 0, null, null);
    }

    public RegistryScreenEventItem withUpdated(UUID modeId, UUID triggerFieldId, String triggerActionCode,
                                               String title, String content, String conditionNote,
                                               UUID targetScreenId, String targetModeCode,
                                               int displayOrder) {
        return new RegistryScreenEventItem(
                id, screenId, workspaceId, modeId, triggerFieldId, triggerActionCode,
                title, content, conditionNote, targetScreenId, targetModeCode,
                displayOrder, status, version, createdAt, Instant.now());
    }
}

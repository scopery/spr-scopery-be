package com.company.scopery.modules.traceability.screeneventitem.application.response;

import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItem;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenEventItemResponse(
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
        String status,
        Instant createdAt) {

    public static RegistryScreenEventItemResponse from(RegistryScreenEventItem item) {
        return new RegistryScreenEventItemResponse(
                item.id(),
                item.screenId(),
                item.workspaceId(),
                item.modeId(),
                item.triggerFieldId(),
                item.triggerActionCode(),
                item.title(),
                item.content(),
                item.conditionNote(),
                item.targetScreenId(),
                item.targetModeCode(),
                item.displayOrder(),
                item.status().name(),
                item.createdAt());
    }
}

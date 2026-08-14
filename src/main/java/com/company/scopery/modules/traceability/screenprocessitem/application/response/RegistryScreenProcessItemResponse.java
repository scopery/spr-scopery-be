package com.company.scopery.modules.traceability.screenprocessitem.application.response;

import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItem;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenProcessItemResponse(
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
        String status,
        Instant createdAt) {

    public static RegistryScreenProcessItemResponse from(RegistryScreenProcessItem item) {
        return new RegistryScreenProcessItemResponse(
                item.id(),
                item.screenId(),
                item.workspaceId(),
                item.modeId(),
                item.targetFieldId(),
                item.title(),
                item.content(),
                item.sourceTable(),
                item.conditionNote(),
                item.displayOrder(),
                item.status().name(),
                item.createdAt());
    }
}

package com.company.scopery.modules.traceability.functionalitem.application.response;

import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItem;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FunctionalItemResponse(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        UUID moduleId,
        String code,
        String title,
        String description,
        String priority,
        String status,
        String type,
        List<String> acceptanceCriteria,
        Instant createdAt,
        Instant updatedAt
) {

    public static FunctionalItemResponse from(FunctionalItem item) {
        return new FunctionalItemResponse(
                item.id(),
                item.projectId(),
                item.workspaceId(),
                item.moduleId(),
                item.code(),
                item.title(),
                item.description(),
                item.priority().name(),
                item.status().name(),
                item.type().name(),
                item.acceptanceCriteria(),
                item.createdAt(),
                item.updatedAt()
        );
    }
}

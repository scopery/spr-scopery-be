package com.company.scopery.modules.traceability.nonfunctionalitem.application.response;

import com.company.scopery.modules.traceability.nonfunctionalitem.domain.model.NonFunctionalItem;

import java.time.Instant;
import java.util.UUID;

public record NonFunctionalItemResponse(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        String code,
        String title,
        String description,
        String category,
        String priority,
        String status,
        String targetMetric,
        String scopeType,
        UUID scopeRefId,
        Instant createdAt,
        Instant updatedAt
) {

    public static NonFunctionalItemResponse from(NonFunctionalItem item) {
        return new NonFunctionalItemResponse(
                item.id(),
                item.projectId(),
                item.workspaceId(),
                item.code(),
                item.title(),
                item.description(),
                item.category() != null ? item.category().name() : null,
                item.priority() != null ? item.priority().name() : null,
                item.status() != null ? item.status().name() : null,
                item.targetMetric(),
                item.scopeType() != null ? item.scopeType().name() : null,
                item.scopeRefId(),
                item.createdAt(),
                item.updatedAt()
        );
    }
}

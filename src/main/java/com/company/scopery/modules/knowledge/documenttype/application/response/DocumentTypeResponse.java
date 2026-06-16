package com.company.scopery.modules.knowledge.documenttype.application.response;

import com.company.scopery.modules.knowledge.documenttype.domain.DocumentType;

import java.time.Instant;
import java.util.UUID;

public record DocumentTypeResponse(
        UUID id,
        String code,
        String name,
        String description,
        String documentScope,
        String status,
        UUID workspaceId,
        boolean isSystem,
        Instant deletedAt,
        Instant createdAt,
        Instant updatedAt) {

    public static DocumentTypeResponse from(DocumentType dt) {
        return new DocumentTypeResponse(
                dt.id(),
                dt.code().value(),
                dt.name(),
                dt.description(),
                dt.documentScope().name(),
                dt.status().name(),
                dt.workspaceId(),
                dt.isSystem(),
                dt.deletedAt(),
                dt.createdAt(),
                dt.updatedAt());
    }
}

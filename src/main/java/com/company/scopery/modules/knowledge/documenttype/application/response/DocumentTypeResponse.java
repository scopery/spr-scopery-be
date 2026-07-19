package com.company.scopery.modules.knowledge.documenttype.application.response;

import com.company.scopery.modules.knowledge.documenttype.domain.model.DocumentType;

import java.time.Instant;
import java.util.UUID;

public record DocumentTypeResponse(
        UUID id,
        String code,
        String name,
        String description,
        String documentScope,
        String status,
        UUID organizationId,
        UUID workspaceId,
        String category,
        String defaultClassification,
        Integer defaultReviewCycleDays,
        String defaultTemplateCode,
        String metadataSchemaJson,
        boolean builtIn,
        boolean isSystem,
        Instant archivedAt,
        UUID archivedBy,
        Instant deletedAt,
        int version,
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
                dt.organizationId(),
                dt.workspaceId(),
                dt.category(),
                dt.defaultClassification() != null ? dt.defaultClassification().name() : null,
                dt.defaultReviewCycleDays(),
                dt.defaultTemplateCode(),
                dt.metadataSchemaJson(),
                dt.builtIn(),
                dt.isSystem(),
                dt.archivedAt(),
                dt.archivedBy(),
                dt.deletedAt(),
                dt.version(),
                dt.createdAt(),
                dt.updatedAt());
    }
}

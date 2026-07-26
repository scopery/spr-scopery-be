package com.company.scopery.modules.documenthub.documentlink.application.response;

import com.company.scopery.modules.documenthub.documentlink.domain.model.DocumentLink;

import java.time.Instant;
import java.util.UUID;

public record DocumentLinkResponse(
        UUID id,
        UUID documentId,
        UUID projectId,
        String linkedEntityType,
        UUID linkedEntityId,
        String relationType,
        Instant archivedAt,
        Instant createdAt,
        String createdBy
) {
    public static DocumentLinkResponse from(DocumentLink link) {
        return new DocumentLinkResponse(
                link.id(),
                link.documentId(),
                link.projectId(),
                link.linkedEntityType().name(),
                link.linkedEntityId(),
                link.relationType().name(),
                link.archivedAt(),
                link.createdAt(),
                link.createdBy()
        );
    }
}

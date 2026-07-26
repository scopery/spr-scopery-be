package com.company.scopery.modules.documenthub.documentlink.domain.model;

import com.company.scopery.modules.documenthub.documentlink.domain.enums.DocumentLinkEntityType;
import com.company.scopery.modules.documenthub.documentlink.domain.enums.DocumentLinkRelationType;

import java.time.Instant;
import java.util.UUID;

public record DocumentLink(
        UUID id,
        UUID documentId,
        UUID projectId,
        DocumentLinkEntityType linkedEntityType,
        UUID linkedEntityId,
        DocumentLinkRelationType relationType,
        Instant archivedAt,
        int version,
        Instant createdAt,
        Instant updatedAt,
        String createdBy
) {
    public static DocumentLink create(UUID documentId,
                                      UUID projectId,
                                      DocumentLinkEntityType linkedEntityType,
                                      UUID linkedEntityId,
                                      DocumentLinkRelationType relationType) {
        return new DocumentLink(
                UUID.randomUUID(),
                documentId,
                projectId,
                linkedEntityType,
                linkedEntityId,
                relationType,
                null,
                0,
                null,
                null,
                null
        );
    }

    public boolean isActive() {
        return archivedAt == null;
    }
}

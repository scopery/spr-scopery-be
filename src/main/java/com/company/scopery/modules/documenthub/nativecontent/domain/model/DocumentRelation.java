package com.company.scopery.modules.documenthub.nativecontent.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DocumentRelation(
        UUID id,
        UUID sourceDocumentId,
        UUID targetDocumentId,
        String relationType,
        String blockId,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentRelation create(UUID sourceDocumentId, UUID targetDocumentId,
                                           String relationType, String blockId) {
        Instant now = Instant.now();
        return new DocumentRelation(UUID.randomUUID(), sourceDocumentId, targetDocumentId,
                relationType, blockId, now, now);
    }
}

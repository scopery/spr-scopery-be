package com.company.scopery.modules.documenthub.nativecontent.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DocumentBlockIndex(
        UUID id,
        UUID documentId,
        String blockId,
        String blockType,
        Integer headingLevel,
        String headingText,
        String plainText,
        int ordinal,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentBlockIndex create(UUID documentId, String blockId, String blockType,
                                             Integer headingLevel, String headingText,
                                             String plainText, int ordinal) {
        Instant now = Instant.now();
        return new DocumentBlockIndex(UUID.randomUUID(), documentId, blockId, blockType,
                headingLevel, headingText, plainText, ordinal, now, now);
    }
}

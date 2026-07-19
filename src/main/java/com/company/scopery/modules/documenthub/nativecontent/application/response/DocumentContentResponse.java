package com.company.scopery.modules.documenthub.nativecontent.application.response;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentContent;

import java.time.Instant;
import java.util.UUID;

public record DocumentContentResponse(
        UUID id,
        UUID documentId,
        long revisionNo,
        String ast,
        String checksum,
        Integer schemaVersion,
        int wordCount,
        int characterCount,
        Instant lastSavedAt,
        UUID lastSavedBy,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentContentResponse from(DocumentContent c) {
        return new DocumentContentResponse(c.id(), c.documentId(), c.revisionNo(), c.ast(), c.checksum(),
                c.schemaVersion(), c.wordCount(), c.characterCount(), c.lastSavedAt(), c.lastSavedBy(),
                c.createdAt(), c.updatedAt());
    }
}

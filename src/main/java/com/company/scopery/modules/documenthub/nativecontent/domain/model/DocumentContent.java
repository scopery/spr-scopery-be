package com.company.scopery.modules.documenthub.nativecontent.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DocumentContent(
        UUID id,
        UUID documentId,
        UUID workspaceId,
        UUID projectId,
        Integer schemaVersion,
        long revisionNo,
        String ast,
        String plainText,
        int wordCount,
        int characterCount,
        String checksum,
        Instant lastSavedAt,
        UUID lastSavedBy,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentContent create(UUID documentId, UUID workspaceId, UUID projectId,
                                          Integer schemaVersion, long revisionNo, String ast,
                                          String plainText, int wordCount, int characterCount,
                                          String checksum, UUID savedBy) {
        Instant now = Instant.now();
        return new DocumentContent(UUID.randomUUID(), documentId, workspaceId, projectId,
                schemaVersion, revisionNo, ast, plainText, wordCount, characterCount,
                checksum, now, savedBy, 0, now, now);
    }

    public DocumentContent withUpdatedContent(long newRevisionNo, String newAst, String newPlainText,
                                               int newWordCount, int newCharacterCount,
                                               String newChecksum, Integer newSchemaVersion, UUID savedBy) {
        Instant now = Instant.now();
        return new DocumentContent(id, documentId, workspaceId, projectId,
                newSchemaVersion != null ? newSchemaVersion : schemaVersion,
                newRevisionNo, newAst, newPlainText, newWordCount, newCharacterCount,
                newChecksum, now, savedBy, version, createdAt, now);
    }
}

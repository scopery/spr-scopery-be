package com.company.scopery.modules.documenthub.nativecontent.domain.model;

import com.company.scopery.modules.documenthub.nativecontent.domain.enums.RevisionType;

import java.time.Instant;
import java.util.UUID;

public record DocumentRevision(
        UUID id,
        UUID documentId,
        UUID workspaceId,
        UUID projectId,
        long revisionNo,
        RevisionType revisionType,
        String ast,
        String plainText,
        String checksum,
        Integer schemaVersion,
        int wordCount,
        int characterCount,
        UUID createdBy,
        Instant createdAt
) {
    public static DocumentRevision create(UUID documentId, UUID workspaceId, UUID projectId,
                                           long revisionNo, RevisionType revisionType,
                                           String ast, String plainText, String checksum,
                                           Integer schemaVersion, int wordCount, int characterCount,
                                           UUID createdBy) {
        return new DocumentRevision(UUID.randomUUID(), documentId, workspaceId, projectId,
                revisionNo, revisionType, ast, plainText, checksum, schemaVersion,
                wordCount, characterCount, createdBy, Instant.now());
    }
}

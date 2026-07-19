package com.company.scopery.modules.documenthub.nativecontent.application.response;

import com.company.scopery.modules.documenthub.nativecontent.domain.model.DocumentRevision;

import java.time.Instant;
import java.util.UUID;

public record DocumentRevisionResponse(
        UUID id,
        UUID documentId,
        long revisionNo,
        String revisionType,
        String ast,
        String checksum,
        Integer schemaVersion,
        int wordCount,
        int characterCount,
        UUID createdBy,
        Instant createdAt
) {
    public static DocumentRevisionResponse from(DocumentRevision r) {
        return new DocumentRevisionResponse(r.id(), r.documentId(), r.revisionNo(), r.revisionType().name(),
                r.ast(), r.checksum(), r.schemaVersion(), r.wordCount(), r.characterCount(),
                r.createdBy(), r.createdAt());
    }

    public static DocumentRevisionResponse fromWithoutAst(DocumentRevision r) {
        return new DocumentRevisionResponse(r.id(), r.documentId(), r.revisionNo(), r.revisionType().name(),
                null, r.checksum(), r.schemaVersion(), r.wordCount(), r.characterCount(),
                r.createdBy(), r.createdAt());
    }
}

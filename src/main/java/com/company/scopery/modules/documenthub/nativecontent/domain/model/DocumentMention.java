package com.company.scopery.modules.documenthub.nativecontent.domain.model;

import java.time.Instant;
import java.util.UUID;

public record DocumentMention(
        UUID id,
        UUID documentId,
        UUID workspaceId,
        UUID projectId,
        String blockId,
        String mentionType,
        String mentionedResourceType,
        UUID mentionedResourceId,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentMention create(UUID documentId, UUID workspaceId, UUID projectId,
                                          String blockId, String mentionType,
                                          String mentionedResourceType, UUID mentionedResourceId) {
        Instant now = Instant.now();
        return new DocumentMention(UUID.randomUUID(), documentId, workspaceId, projectId,
                blockId, mentionType, mentionedResourceType, mentionedResourceId, now, now);
    }
}

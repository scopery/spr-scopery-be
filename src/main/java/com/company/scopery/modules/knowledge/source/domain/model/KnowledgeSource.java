package com.company.scopery.modules.knowledge.source.domain.model;

import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceStatus;
import com.company.scopery.modules.knowledge.source.domain.enums.KnowledgeSourceType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record KnowledgeSource(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        KnowledgeSourceType sourceType,
        UUID sourceRefId,
        UUID sourceVersionRefId,
        String title,
        String language,
        String classification,
        String contentHash,
        String permissionSignature,
        List<String> aclTokens,
        KnowledgeSourceStatus status,
        Instant lastObservedAt,
        Instant lastIndexedAt,
        Instant createdAt,
        UUID createdBy,
        Instant updatedAt,
        UUID updatedBy,
        long version
) {
    public KnowledgeSource withStatus(KnowledgeSourceStatus newStatus, Instant now) {
        Instant indexed = newStatus == KnowledgeSourceStatus.INDEXED ? now : this.lastIndexedAt;
        return new KnowledgeSource(id, workspaceId, projectId, sourceType, sourceRefId, sourceVersionRefId,
                title, language, classification, contentHash, permissionSignature, aclTokens,
                newStatus, now, indexed, createdAt, createdBy, now, updatedBy, version + 1);
    }
}

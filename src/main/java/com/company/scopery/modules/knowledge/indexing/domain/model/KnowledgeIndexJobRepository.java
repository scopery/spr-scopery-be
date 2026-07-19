package com.company.scopery.modules.knowledge.indexing.domain.model;

import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KnowledgeIndexJobRepository {
    KnowledgeIndexJob save(KnowledgeIndexJob job);
    Optional<KnowledgeIndexJob> findById(UUID id);
    Optional<KnowledgeIndexJob> findByIdempotencyKey(String idempotencyKey);
    List<KnowledgeIndexJob> findByWorkspaceId(UUID workspaceId);
    List<KnowledgeIndexJob> findByStatus(IndexJobStatus status);
}

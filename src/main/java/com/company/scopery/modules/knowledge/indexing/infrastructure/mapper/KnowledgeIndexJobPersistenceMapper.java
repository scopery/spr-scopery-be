package com.company.scopery.modules.knowledge.indexing.infrastructure.mapper;

import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobStatus;
import com.company.scopery.modules.knowledge.indexing.domain.enums.IndexJobType;
import com.company.scopery.modules.knowledge.indexing.domain.model.KnowledgeIndexJob;
import com.company.scopery.modules.knowledge.indexing.infrastructure.persistence.KnowledgeIndexJobJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeIndexJobPersistenceMapper {

    public KnowledgeIndexJob toDomain(KnowledgeIndexJobJpaEntity entity) {
        return new KnowledgeIndexJob(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                entity.getSourceId(),
                entity.getEmbeddingProfileId(),
                IndexJobType.valueOf(entity.getJobType()),
                IndexJobStatus.valueOf(entity.getJobStatus()),
                entity.getIdempotencyKey(),
                entity.getTargetIndexName(),
                entity.getAttemptCount() != null ? entity.getAttemptCount() : 0,
                entity.getProcessedCount() != null ? entity.getProcessedCount() : 0,
                entity.getSuccessCount() != null ? entity.getSuccessCount() : 0,
                entity.getFailureCount() != null ? entity.getFailureCount() : 0,
                entity.getErrorCode(),
                entity.getErrorMessageRedacted(),
                entity.getQueuedAt(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getCreatedBy()
        );
    }

    public KnowledgeIndexJobJpaEntity toJpaEntity(KnowledgeIndexJob domain) {
        KnowledgeIndexJobJpaEntity entity = new KnowledgeIndexJobJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setSourceId(domain.sourceId());
        entity.setEmbeddingProfileId(domain.embeddingProfileId());
        entity.setJobType(domain.jobType().name());
        entity.setJobStatus(domain.jobStatus().name());
        entity.setIdempotencyKey(domain.idempotencyKey());
        entity.setTargetIndexName(domain.targetIndexName());
        entity.setAttemptCount(domain.attemptCount());
        entity.setProcessedCount(domain.processedCount());
        entity.setSuccessCount(domain.successCount());
        entity.setFailureCount(domain.failureCount());
        entity.setErrorCode(domain.errorCode());
        entity.setErrorMessageRedacted(domain.errorMessageRedacted());
        entity.setQueuedAt(domain.queuedAt());
        entity.setStartedAt(domain.startedAt());
        entity.setCompletedAt(domain.completedAt());
        entity.setCreatedBy(domain.createdBy());
        return entity;
    }
}

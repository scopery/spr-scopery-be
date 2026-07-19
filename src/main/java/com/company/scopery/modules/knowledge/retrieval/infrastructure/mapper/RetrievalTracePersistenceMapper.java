package com.company.scopery.modules.knowledge.retrieval.infrastructure.mapper;

import com.company.scopery.modules.knowledge.retrieval.domain.model.RetrievalTrace;
import com.company.scopery.modules.knowledge.retrieval.infrastructure.persistence.RetrievalTraceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RetrievalTracePersistenceMapper {

    public RetrievalTrace toDomain(RetrievalTraceJpaEntity entity) {
        return new RetrievalTrace(
                entity.getId(),
                entity.getWorkspaceId(),
                entity.getProjectId(),
                entity.getActorId(),
                entity.getQueryHash(),
                entity.getRetrievalMode(),
                entity.getLexicalCandidateCount() != null ? entity.getLexicalCandidateCount() : 0,
                entity.getVectorCandidateCount() != null ? entity.getVectorCandidateCount() : 0,
                entity.getGraphCandidateCount() != null ? entity.getGraphCandidateCount() : 0,
                entity.getReturnedCount() != null ? entity.getReturnedCount() : 0,
                entity.getDurationMs() != null ? entity.getDurationMs() : 0,
                entity.getResultStatus(),
                entity.getErrorCode(),
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );
    }

    public RetrievalTraceJpaEntity toJpaEntity(RetrievalTrace domain) {
        RetrievalTraceJpaEntity entity = new RetrievalTraceJpaEntity();
        entity.setId(domain.id());
        entity.setWorkspaceId(domain.workspaceId());
        entity.setProjectId(domain.projectId());
        entity.setActorId(domain.actorId());
        entity.setQueryHash(domain.queryHash());
        entity.setRetrievalMode(domain.retrievalMode());
        entity.setLexicalCandidateCount(domain.lexicalCandidateCount());
        entity.setVectorCandidateCount(domain.vectorCandidateCount());
        entity.setGraphCandidateCount(domain.graphCandidateCount());
        entity.setReturnedCount(domain.returnedCount());
        entity.setDurationMs(domain.durationMs());
        entity.setResultStatus(domain.resultStatus());
        entity.setErrorCode(domain.errorCode());
        entity.setCreatedAt(domain.createdAt());
        entity.setExpiresAt(domain.expiresAt());
        return entity;
    }
}

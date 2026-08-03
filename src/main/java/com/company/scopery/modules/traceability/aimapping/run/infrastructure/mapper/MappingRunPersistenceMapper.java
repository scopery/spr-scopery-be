package com.company.scopery.modules.traceability.aimapping.run.infrastructure.mapper;

import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRelationType;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingRunStatus;
import com.company.scopery.modules.traceability.aimapping.run.domain.enums.MappingScope;
import com.company.scopery.modules.traceability.aimapping.run.domain.model.MappingRun;
import com.company.scopery.modules.traceability.aimapping.run.infrastructure.persistence.MappingRunJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MappingRunPersistenceMapper {

    public MappingRun toDomain(MappingRunJpaEntity entity) {
        return new MappingRun(
                entity.getId(),
                entity.getProjectId(),
                MappingRelationType.valueOf(entity.getRelationType()),
                MappingScope.valueOf(entity.getScope()),
                entity.getModelDeploymentId(),
                entity.getPromptKey(),
                entity.getPromptVersion(),
                entity.getSummaryVersion(),
                entity.getCandidateLimit(),
                entity.getRequestedBy(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                MappingRunStatus.valueOf(entity.getStatus()),
                entity.getSourceCount(),
                entity.getSuggestionCount(),
                entity.getTokenUsageJson(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public MappingRunJpaEntity toJpaEntity(MappingRun domain) {
        MappingRunJpaEntity entity = new MappingRunJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setRelationType(domain.relationType().name());
        entity.setScope(domain.scope().name());
        entity.setModelDeploymentId(domain.modelDeploymentId());
        entity.setPromptKey(domain.promptKey());
        entity.setPromptVersion(domain.promptVersion());
        entity.setSummaryVersion(domain.summaryVersion());
        entity.setCandidateLimit(domain.candidateLimit());
        entity.setRequestedBy(domain.requestedBy());
        entity.setStartedAt(domain.startedAt());
        entity.setCompletedAt(domain.completedAt());
        entity.setStatus(domain.status().name());
        entity.setSourceCount(domain.sourceCount());
        entity.setSuggestionCount(domain.suggestionCount());
        entity.setTokenUsageJson(domain.tokenUsageJson());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
        }
        return entity;
    }
}

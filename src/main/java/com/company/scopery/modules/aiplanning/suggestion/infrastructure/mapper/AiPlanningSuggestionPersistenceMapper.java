package com.company.scopery.modules.aiplanning.suggestion.infrastructure.mapper;

import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionStatus;
import com.company.scopery.modules.aiplanning.suggestion.domain.enums.SuggestionType;
import com.company.scopery.modules.aiplanning.suggestion.domain.model.AiPlanningSuggestion;
import com.company.scopery.modules.aiplanning.suggestion.infrastructure.persistence.AiPlanningSuggestionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiPlanningSuggestionPersistenceMapper {
    public AiPlanningSuggestion toDomain(AiPlanningSuggestionJpaEntity e) {
        return new AiPlanningSuggestion(
                e.getId(), e.getPlanningRunId(), e.getProjectId(), e.getWorkspaceId(),
                SuggestionType.valueOf(e.getSuggestionType()), e.getTitle(), e.getSummary(), e.getRationale(),
                e.getConfidenceLabel(), SuggestionStatus.valueOf(e.getStatus()), e.getSourceReferencesJson(),
                e.getReviewedAt(), e.getReviewedBy(), e.getAppliedAt(), e.getAppliedBy(),
                e.getRejectedAt(), e.getRejectedBy(), e.getRejectionReason(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public AiPlanningSuggestionJpaEntity toJpaEntity(AiPlanningSuggestion d) {
        AiPlanningSuggestionJpaEntity e = new AiPlanningSuggestionJpaEntity();
        e.setId(d.id());
        e.setPlanningRunId(d.planningRunId());
        e.setProjectId(d.projectId());
        e.setWorkspaceId(d.workspaceId());
        e.setSuggestionType(d.suggestionType().name());
        e.setTitle(d.title());
        e.setSummary(d.summary());
        e.setRationale(d.rationale());
        e.setConfidenceLabel(d.confidenceLabel());
        e.setStatus(d.status().name());
        e.setSourceReferencesJson(d.sourceReferencesJson());
        e.setReviewedAt(d.reviewedAt());
        e.setReviewedBy(d.reviewedBy());
        e.setAppliedAt(d.appliedAt());
        e.setAppliedBy(d.appliedBy());
        e.setRejectedAt(d.rejectedAt());
        e.setRejectedBy(d.rejectedBy());
        e.setRejectionReason(d.rejectionReason());
        e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}

package com.company.scopery.modules.aiplanning.suggestionitem.infrastructure.mapper;

import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemOperation;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemStatus;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.enums.SuggestionItemType;
import com.company.scopery.modules.aiplanning.suggestionitem.domain.model.AiPlanningSuggestionItem;
import com.company.scopery.modules.aiplanning.suggestionitem.infrastructure.persistence.AiPlanningSuggestionItemJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AiPlanningSuggestionItemPersistenceMapper {
    public AiPlanningSuggestionItem toDomain(AiPlanningSuggestionItemJpaEntity e) {
        return new AiPlanningSuggestionItem(
                e.getId(), e.getSuggestionId(), e.getProjectId(),
                SuggestionItemType.valueOf(e.getItemType()), e.getTargetType(), e.getTargetId(),
                SuggestionItemOperation.valueOf(e.getOperation()), e.getTitle(), e.getDescription(),
                e.getProposedPayloadJson(), e.getRationale(), e.getConfidenceLabel(),
                SuggestionItemStatus.valueOf(e.getStatus()), e.getApplyAction(), e.getApplyResultJson(),
                e.getReviewedAt(), e.getReviewedBy(), e.getAppliedAt(), e.getAppliedBy(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public AiPlanningSuggestionItemJpaEntity toJpaEntity(AiPlanningSuggestionItem d) {
        AiPlanningSuggestionItemJpaEntity e = new AiPlanningSuggestionItemJpaEntity();
        e.setId(d.id());
        e.setSuggestionId(d.suggestionId());
        e.setProjectId(d.projectId());
        e.setItemType(d.itemType().name());
        e.setTargetType(d.targetType());
        e.setTargetId(d.targetId());
        e.setOperation(d.operation().name());
        e.setTitle(d.title());
        e.setDescription(d.description());
        e.setProposedPayloadJson(d.proposedPayloadJson());
        e.setRationale(d.rationale());
        e.setConfidenceLabel(d.confidenceLabel());
        e.setStatus(d.status().name());
        e.setApplyAction(d.applyAction());
        e.setApplyResultJson(d.applyResultJson());
        e.setReviewedAt(d.reviewedAt());
        e.setReviewedBy(d.reviewedBy());
        e.setAppliedAt(d.appliedAt());
        e.setAppliedBy(d.appliedBy());
        e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}

package com.company.scopery.modules.elicitation.suggestion.infrastructure.mapper;

import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionItemImpact;
import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionItemStatus;
import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionStatus;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestion;
import com.company.scopery.modules.elicitation.suggestion.domain.model.ElicitationSuggestionItem;
import com.company.scopery.modules.elicitation.suggestion.infrastructure.persistence.ElicitationSuggestionItemJpaEntity;
import com.company.scopery.modules.elicitation.suggestion.infrastructure.persistence.ElicitationSuggestionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ElicitationSuggestionPersistenceMapper {

    public ElicitationSuggestionJpaEntity toJpaEntity(ElicitationSuggestion domain) {
        ElicitationSuggestionJpaEntity entity = new ElicitationSuggestionJpaEntity();
        entity.setId(domain.id());
        entity.setRoundId(domain.roundId());
        entity.setOverallSummary(domain.overallSummary());
        entity.setStatus(domain.status().name());
        entity.setAiRawResponse(domain.aiRawResponse());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public ElicitationSuggestion toDomain(ElicitationSuggestionJpaEntity entity) {
        return ElicitationSuggestion.reconstitute(
                entity.getId(),
                entity.getRoundId(),
                entity.getOverallSummary(),
                SuggestionStatus.valueOf(entity.getStatus()),
                entity.getAiRawResponse(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ElicitationSuggestionItemJpaEntity toItemJpaEntity(ElicitationSuggestionItem domain) {
        ElicitationSuggestionItemJpaEntity entity = new ElicitationSuggestionItemJpaEntity();
        entity.setId(domain.id());
        entity.setSuggestionId(domain.suggestionId());
        entity.setSequence(domain.sequence());
        entity.setAction(domain.action());
        entity.setTargetEntityType(domain.targetEntityType());
        entity.setTargetEntityId(domain.targetEntityId());
        entity.setTargetEntityName(domain.targetEntityName());
        entity.setRationale(domain.rationale());
        entity.setChangesJson(domain.changesJson());
        entity.setPreconditionActionsJson(domain.preconditionActionsJson());
        entity.setEstimatedImpact(domain.estimatedImpact().name());
        entity.setStatus(domain.status().name());
        entity.setExecutedAt(domain.executedAt());
        entity.setErrorMessage(domain.errorMessage());
        entity.setRollbackDataJson(domain.rollbackDataJson());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public ElicitationSuggestionItem toItemDomain(ElicitationSuggestionItemJpaEntity entity) {
        return ElicitationSuggestionItem.reconstitute(
                entity.getId(),
                entity.getSuggestionId(),
                entity.getSequence(),
                entity.getAction(),
                entity.getTargetEntityType(),
                entity.getTargetEntityId(),
                entity.getTargetEntityName(),
                entity.getRationale(),
                entity.getChangesJson(),
                entity.getPreconditionActionsJson(),
                SuggestionItemImpact.valueOf(entity.getEstimatedImpact()),
                SuggestionItemStatus.valueOf(entity.getStatus()),
                entity.getExecutedAt(),
                entity.getErrorMessage(),
                entity.getRollbackDataJson(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

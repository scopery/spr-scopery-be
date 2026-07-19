package com.company.scopery.modules.aiassistant.infrastructure.mapper;

import com.company.scopery.modules.aiassistant.domain.model.AiSuggestedQuestion;
import com.company.scopery.modules.aiassistant.infrastructure.persistence.AiSuggestedQuestionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiSuggestedQuestionPersistenceMapper {

    public AiSuggestedQuestionJpaEntity toJpaEntity(AiSuggestedQuestion domain) {
        AiSuggestedQuestionJpaEntity entity = new AiSuggestedQuestionJpaEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setPageCode(domain.pageCode());
        entity.setEntityType(domain.entityType());
        entity.setActionCode(domain.actionCode());
        entity.setLocale(domain.locale());
        entity.setQuestionText(domain.questionText());
        entity.setDisplayOrder(domain.displayOrder());
        entity.setStatus(domain.status());
        if (domain.createdAt() != null) {
            entity.setCreatedAt(domain.createdAt());
            entity.setVersion(domain.version());
        }
        return entity;
    }

    public AiSuggestedQuestion toDomain(AiSuggestedQuestionJpaEntity entity) {
        return AiSuggestedQuestion.reconstitute(
                entity.getId(),
                entity.getCode(),
                entity.getPageCode(),
                entity.getEntityType(),
                entity.getActionCode(),
                entity.getLocale(),
                entity.getQuestionText(),
                entity.getDisplayOrder(),
                entity.getStatus(),
                entity.getCreatedAt(),
                parseUuid(entity.getCreatedBy()),
                entity.getUpdatedAt(),
                parseUuid(entity.getUpdatedBy()),
                entity.getVersion()
        );
    }

    private static UUID parseUuid(String value) {
        if (value == null) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

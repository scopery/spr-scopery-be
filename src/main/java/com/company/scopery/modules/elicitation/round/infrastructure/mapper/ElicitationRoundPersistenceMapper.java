package com.company.scopery.modules.elicitation.round.infrastructure.mapper;

import com.company.scopery.modules.elicitation.question.domain.enums.ClarityLevel;
import com.company.scopery.modules.elicitation.round.domain.enums.RoundStatus;
import com.company.scopery.modules.elicitation.round.domain.model.ElicitationRound;
import com.company.scopery.modules.elicitation.round.infrastructure.persistence.ElicitationRoundJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ElicitationRoundPersistenceMapper {

    public ElicitationRoundJpaEntity toJpaEntity(ElicitationRound domain) {
        ElicitationRoundJpaEntity entity = new ElicitationRoundJpaEntity();
        entity.setId(domain.id());
        entity.setSessionId(domain.sessionId());
        entity.setRoundNumber(domain.roundNumber());
        entity.setQuestionsJson(domain.questionsJson());
        entity.setOverallClarity(domain.overallClarity() != null ? domain.overallClarity().name() : null);
        entity.setStatus(domain.status().name());
        entity.setSubmittedAt(domain.submittedAt());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public ElicitationRound toDomain(ElicitationRoundJpaEntity entity) {
        return ElicitationRound.reconstitute(
                entity.getId(),
                entity.getSessionId(),
                entity.getRoundNumber(),
                entity.getQuestionsJson(),
                entity.getOverallClarity() != null ? ClarityLevel.valueOf(entity.getOverallClarity()) : null,
                RoundStatus.valueOf(entity.getStatus()),
                entity.getSubmittedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

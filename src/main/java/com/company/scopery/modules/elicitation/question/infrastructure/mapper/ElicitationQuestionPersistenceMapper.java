package com.company.scopery.modules.elicitation.question.infrastructure.mapper;

import com.company.scopery.modules.elicitation.question.domain.enums.ClarityLevel;
import com.company.scopery.modules.elicitation.question.domain.enums.QuestionSource;
import com.company.scopery.modules.elicitation.question.domain.enums.QuestionStatus;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;
import com.company.scopery.modules.elicitation.question.infrastructure.persistence.ElicitationQuestionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ElicitationQuestionPersistenceMapper {

    public ElicitationQuestionJpaEntity toJpaEntity(ElicitationQuestion domain) {
        ElicitationQuestionJpaEntity entity = new ElicitationQuestionJpaEntity();
        entity.setId(domain.id());
        entity.setSessionId(domain.sessionId());
        entity.setSequence(domain.sequence());
        entity.setQuestionText(domain.questionText());
        entity.setAnswerText(domain.answerText());
        entity.setClarityLevel(domain.clarityLevel() != null ? domain.clarityLevel().name() : null);
        entity.setAiFeedback(domain.aiFeedback());
        entity.setStatus(domain.status().name());
        entity.setSource(domain.source().name());
        entity.setParentQuestionId(domain.parentQuestionId());
        entity.setAnsweredAt(domain.answeredAt());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public ElicitationQuestion toDomain(ElicitationQuestionJpaEntity entity) {
        return ElicitationQuestion.reconstitute(
                entity.getId(),
                entity.getSessionId(),
                entity.getSequence(),
                entity.getQuestionText(),
                entity.getAnswerText(),
                entity.getClarityLevel() != null ? ClarityLevel.valueOf(entity.getClarityLevel()) : null,
                entity.getAiFeedback(),
                QuestionStatus.valueOf(entity.getStatus()),
                QuestionSource.valueOf(entity.getSource()),
                entity.getParentQuestionId(),
                entity.getAnsweredAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

package com.company.scopery.modules.elicitation.question.infrastructure.mapper;

import com.company.scopery.modules.elicitation.question.domain.enums.ClarityLevel;
import com.company.scopery.modules.elicitation.question.domain.enums.QuestionSource;
import com.company.scopery.modules.elicitation.question.domain.enums.QuestionStatus;
import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;
import com.company.scopery.modules.elicitation.question.infrastructure.persistence.ElicitationQuestionJpaEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ElicitationQuestionPersistenceMapper {

    private final ObjectMapper objectMapper;

    public ElicitationQuestionPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ElicitationQuestionJpaEntity toJpaEntity(ElicitationQuestion domain) {
        ElicitationQuestionJpaEntity entity = new ElicitationQuestionJpaEntity();
        entity.setId(domain.id());
        entity.setSessionId(domain.sessionId());
        entity.setRoundId(domain.roundId());
        entity.setSequence(domain.sequence());
        entity.setQuestionText(domain.questionText());
        entity.setAnswerText(domain.answerText());
        entity.setClarityLevel(domain.clarityLevel() != null ? domain.clarityLevel().name() : null);
        entity.setAiFeedback(domain.aiFeedback());
        entity.setStatus(domain.status().name());
        entity.setSource(domain.source().name());
        entity.setParentQuestionId(domain.parentQuestionId());
        entity.setAnsweredAt(domain.answeredAt());
        entity.setSuggestedAnswers(toJson(domain.suggestedAnswers()));
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }

    public ElicitationQuestion toDomain(ElicitationQuestionJpaEntity entity) {
        return ElicitationQuestion.reconstitute(
                entity.getId(),
                entity.getSessionId(),
                entity.getRoundId(),
                entity.getSequence(),
                entity.getQuestionText(),
                entity.getAnswerText(),
                entity.getClarityLevel() != null ? ClarityLevel.valueOf(entity.getClarityLevel()) : null,
                entity.getAiFeedback(),
                QuestionStatus.valueOf(entity.getStatus()),
                QuestionSource.valueOf(entity.getSource()),
                entity.getParentQuestionId(),
                entity.getAnsweredAt(),
                fromJson(entity.getSuggestedAnswers()),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String toJson(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> fromJson(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return null;
        }
    }
}

package com.company.scopery.modules.elicitation.question.application.response;

import com.company.scopery.modules.elicitation.question.domain.model.ElicitationQuestion;

import java.time.Instant;
import java.util.UUID;

public record ElicitationQuestionResponse(
        UUID id,
        UUID sessionId,
        int sequence,
        String questionText,
        String answerText,
        String clarityLevel,
        String aiFeedback,
        String status,
        String source,
        UUID parentQuestionId,
        Instant answeredAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static ElicitationQuestionResponse from(ElicitationQuestion question) {
        return new ElicitationQuestionResponse(
                question.id(),
                question.sessionId(),
                question.sequence(),
                question.questionText(),
                question.answerText(),
                question.clarityLevel() != null ? question.clarityLevel().name() : null,
                question.aiFeedback(),
                question.status().name(),
                question.source().name(),
                question.parentQuestionId(),
                question.answeredAt(),
                question.createdAt(),
                question.updatedAt()
        );
    }
}

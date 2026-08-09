package com.company.scopery.modules.elicitation.question.http.request;

import jakarta.validation.constraints.NotBlank;

public record AnswerQuestionRequest(
        @NotBlank String answerText
) {}

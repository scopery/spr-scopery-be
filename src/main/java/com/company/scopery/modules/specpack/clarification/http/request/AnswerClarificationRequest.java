package com.company.scopery.modules.specpack.clarification.http.request;

import jakarta.validation.constraints.NotBlank;

public record AnswerClarificationRequest(
        @NotBlank String answer
) {}

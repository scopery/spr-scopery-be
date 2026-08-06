package com.company.scopery.modules.specpack.clarification.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateClarificationRequest(
        @NotBlank String code,
        @NotBlank String question,
        @NotBlank String priority,
        @NotBlank String source
) {}

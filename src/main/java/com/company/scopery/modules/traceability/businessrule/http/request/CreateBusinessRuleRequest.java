package com.company.scopery.modules.traceability.businessrule.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateBusinessRuleRequest(
        @NotBlank String code,
        @NotBlank String title,
        String description,
        @NotBlank String severity
) {}

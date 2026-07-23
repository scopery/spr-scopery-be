package com.company.scopery.modules.traceability.businessrule.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateBusinessRuleRequest(
        @NotBlank String title,
        String description,
        String severity,
        String status
) {}

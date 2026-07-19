package com.company.scopery.modules.notification.emailrule.http.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRuleRequest(
        @NotBlank String name,
        String description,
        @NotBlank String recipientStrategy,
        String recipientConfigJson,
        int priority,
        Boolean mandatory,
        Boolean allowSensitiveVariables
) {}

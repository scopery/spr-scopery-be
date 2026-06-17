package com.company.scopery.modules.notification.emailtemplate.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateEmailTemplateRequest(
        @NotBlank String name,
        String description
) {}

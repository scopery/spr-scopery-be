package com.company.scopery.modules.notification.emailtemplate.http.request;

import jakarta.validation.constraints.NotBlank;

public record CreateEmailTemplateVersionRequest(
        @NotBlank String subjectTemplate,
        @NotBlank String htmlBodyTemplate,
        String textBodyTemplate
) {}

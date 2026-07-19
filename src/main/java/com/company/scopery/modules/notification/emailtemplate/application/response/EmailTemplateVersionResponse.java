package com.company.scopery.modules.notification.emailtemplate.application.response;

import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplateVersion;

import java.time.Instant;
import java.util.UUID;

public record EmailTemplateVersionResponse(
        UUID id,
        UUID templateId,
        int versionNumber,
        String subjectTemplate,
        String htmlBodyTemplate,
        String textBodyTemplate,
        String status,
        Instant publishedAt,
        UUID publishedBy,
        Instant createdAt,
        Instant updatedAt
) {
    public static EmailTemplateVersionResponse from(EmailTemplateVersion v) {
        return new EmailTemplateVersionResponse(
                v.id(), v.templateId(), v.versionNumber(),
                v.subjectTemplate(), v.htmlBodyTemplate(), v.textBodyTemplate(),
                v.status().name(), v.publishedAt(), v.publishedBy(),
                v.createdAt(), v.updatedAt());
    }
}

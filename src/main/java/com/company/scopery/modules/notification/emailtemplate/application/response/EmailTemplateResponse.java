package com.company.scopery.modules.notification.emailtemplate.application.response;

import com.company.scopery.modules.notification.emailtemplate.domain.model.EmailTemplate;

import java.time.Instant;
import java.util.UUID;

public record EmailTemplateResponse(
        UUID id,
        String code,
        String name,
        String description,
        String scope,
        UUID workspaceId,
        UUID eventDefinitionId,
        String status,
        UUID currentVersionId,
        Instant createdAt,
        Instant updatedAt
) {
    public static EmailTemplateResponse from(EmailTemplate t) {
        return new EmailTemplateResponse(
                t.id(), t.code().value(), t.name(), t.description(),
                t.scope().name(), t.workspaceId(), t.eventDefinitionId(),
                t.status().name(), t.currentVersionId(),
                t.createdAt(), t.updatedAt());
    }
}

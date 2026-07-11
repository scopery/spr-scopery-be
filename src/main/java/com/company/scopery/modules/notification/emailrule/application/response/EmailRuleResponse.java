package com.company.scopery.modules.notification.emailrule.application.response;

import com.company.scopery.modules.notification.emailrule.domain.model.EmailRule;

import java.time.Instant;
import java.util.UUID;

public record EmailRuleResponse(
        UUID id,
        String code,
        String name,
        String description,
        String scope,
        UUID workspaceId,
        UUID eventDefinitionId,
        UUID templateId,
        String recipientStrategy,
        String recipientConfigJson,
        int priority,
        boolean enabled,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static EmailRuleResponse from(EmailRule r) {
        return new EmailRuleResponse(
                r.id(), r.code(), r.name(), r.description(),
                r.scope().name(), r.workspaceId(), r.eventDefinitionId(), r.templateId(),
                r.recipientStrategy().name(), r.recipientConfigJson(),
                r.priority(), r.enabled(), r.status().name(),
                r.createdAt(), r.updatedAt());
    }
}

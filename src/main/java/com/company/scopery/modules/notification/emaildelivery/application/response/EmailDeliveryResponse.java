package com.company.scopery.modules.notification.emaildelivery.application.response;

import com.company.scopery.modules.notification.emaildelivery.domain.EmailDelivery;

import java.time.Instant;
import java.util.UUID;

public record EmailDeliveryResponse(
        UUID id,
        UUID ruleId,
        UUID templateId,
        UUID templateVersionId,
        UUID eventDefinitionId,
        UUID workspaceId,
        String toEmail,
        String renderedSubject,
        String status,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {
    public static EmailDeliveryResponse from(EmailDelivery d) {
        return new EmailDeliveryResponse(
                d.id(), d.ruleId(), d.templateId(), d.templateVersionId(),
                d.eventDefinitionId(), d.workspaceId(),
                d.toEmail(), d.renderedSubject(),
                d.status().name(), d.failureReason(),
                d.createdAt(), d.updatedAt());
    }
}

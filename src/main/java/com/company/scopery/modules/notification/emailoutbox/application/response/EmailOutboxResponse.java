package com.company.scopery.modules.notification.emailoutbox.application.response;

import com.company.scopery.modules.notification.emailoutbox.domain.model.EmailOutbox;

import java.time.Instant;
import java.util.UUID;

public record EmailOutboxResponse(
        UUID id,
        UUID deliveryId,
        String toEmail,
        String subject,
        String providerType,
        String status,
        String failureReason,
        String providerMessageId,
        int retryCount,
        Instant scheduledAt,
        Instant sentAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static EmailOutboxResponse from(EmailOutbox o) {
        return new EmailOutboxResponse(
                o.id(), o.deliveryId(), o.toEmail(), o.subject(),
                o.providerType().name(), o.status().name(),
                o.failureReason(), o.providerMessageId(),
                o.retryCount(), o.scheduledAt(), o.sentAt(),
                o.createdAt(), o.updatedAt());
    }
}

package com.company.scopery.modules.notification.emailoutbox.domain.model;

import com.company.scopery.modules.notification.emailoutbox.domain.enums.EmailOutboxStatus;
import com.company.scopery.modules.notification.emailoutbox.domain.enums.EmailProviderType;

import java.time.Instant;
import java.util.UUID;

public class EmailOutbox {

    private final UUID id;
    private final UUID deliveryId;
    private final String toEmail;
    private final String subject;
    private final String htmlBody;
    private final String textBody;
    private final EmailProviderType providerType;
    private final String dedupKey;
    private EmailOutboxStatus status;
    private String failureReason;
    private String providerMessageId;
    private int retryCount;
    private Instant scheduledAt;
    private Instant sentAt;
    private final Instant createdAt;
    private Instant updatedAt;

    private EmailOutbox(UUID id, UUID deliveryId, String toEmail, String subject,
                        String htmlBody, String textBody, EmailProviderType providerType,
                        String dedupKey, EmailOutboxStatus status, String failureReason,
                        String providerMessageId, int retryCount, Instant scheduledAt,
                        Instant sentAt, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.deliveryId = deliveryId;
        this.toEmail = toEmail;
        this.subject = subject;
        this.htmlBody = htmlBody;
        this.textBody = textBody;
        this.providerType = providerType;
        this.dedupKey = dedupKey;
        this.status = status;
        this.failureReason = failureReason;
        this.providerMessageId = providerMessageId;
        this.retryCount = retryCount;
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EmailOutbox create(UUID deliveryId, EmailMessage message,
                                     EmailProviderType providerType, String dedupKey) {
        if (dedupKey == null || dedupKey.isBlank()) {
            throw new IllegalArgumentException("dedupKey must not be blank");
        }
        Instant now = Instant.now();
        return new EmailOutbox(UUID.randomUUID(), deliveryId,
                message.toEmail(), message.subject(), message.htmlBody(), message.textBody(),
                providerType, dedupKey.trim(), EmailOutboxStatus.PENDING, null, null,
                0, now, null, now, now);
    }

    /** @deprecated Prefer create(deliveryId, message, providerType, dedupKey). */
    public static EmailOutbox create(UUID deliveryId, EmailMessage message, EmailProviderType providerType) {
        return create(deliveryId, message, providerType, UUID.randomUUID().toString());
    }

    public static EmailOutbox reconstitute(UUID id, UUID deliveryId, String toEmail, String subject,
                                            String htmlBody, String textBody, EmailProviderType providerType,
                                            String dedupKey, EmailOutboxStatus status, String failureReason,
                                            String providerMessageId, int retryCount,
                                            Instant scheduledAt, Instant sentAt,
                                            Instant createdAt, Instant updatedAt) {
        return new EmailOutbox(id, deliveryId, toEmail, subject, htmlBody, textBody, providerType,
                dedupKey, status, failureReason, providerMessageId, retryCount,
                scheduledAt, sentAt, createdAt, updatedAt);
    }

    public void markProcessing() {
        this.status = EmailOutboxStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markSent(String providerMessageId) {
        this.status = EmailOutboxStatus.SENT;
        this.providerMessageId = providerMessageId;
        this.sentAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void markFailed(String reason) {
        this.status = EmailOutboxStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
    }

    public void scheduleRetry(int delaySeconds) {
        this.status = EmailOutboxStatus.RETRY_SCHEDULED;
        this.retryCount++;
        this.scheduledAt = Instant.now().plusSeconds(delaySeconds);
        this.failureReason = null;
        this.updatedAt = Instant.now();
    }

    public void markDeadLetter(String reason) {
        this.status = EmailOutboxStatus.DEAD_LETTER;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
    }

    public void markCancelled() {
        this.status = EmailOutboxStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void resetForManualRetry() {
        this.status = EmailOutboxStatus.PENDING;
        this.scheduledAt = Instant.now();
        this.failureReason = null;
        this.updatedAt = Instant.now();
    }

    public UUID id() { return id; }
    public UUID deliveryId() { return deliveryId; }
    public String toEmail() { return toEmail; }
    public String subject() { return subject; }
    public String htmlBody() { return htmlBody; }
    public String textBody() { return textBody; }
    public EmailProviderType providerType() { return providerType; }
    public String dedupKey() { return dedupKey; }
    public EmailOutboxStatus status() { return status; }
    public String failureReason() { return failureReason; }
    public String providerMessageId() { return providerMessageId; }
    public int retryCount() { return retryCount; }
    public Instant scheduledAt() { return scheduledAt; }
    public Instant sentAt() { return sentAt; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}

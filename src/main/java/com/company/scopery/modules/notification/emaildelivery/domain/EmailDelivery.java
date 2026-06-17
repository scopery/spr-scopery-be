package com.company.scopery.modules.notification.emaildelivery.domain;

import java.time.Instant;
import java.util.UUID;

public class EmailDelivery {

    private final UUID id;
    private final UUID ruleId;
    private final UUID templateId;
    private final UUID templateVersionId;
    private final UUID eventDefinitionId;
    private final UUID workspaceId;
    private final String toEmail;
    private final String renderedSubject;
    private final String renderedHtmlBody;
    private final String renderedTextBody;
    private final String eventPayloadJson;
    private EmailDeliveryStatus status;
    private String failureReason;
    private final Instant createdAt;
    private Instant updatedAt;

    private EmailDelivery(UUID id, UUID ruleId, UUID templateId, UUID templateVersionId,
                          UUID eventDefinitionId, UUID workspaceId,
                          String toEmail, String renderedSubject,
                          String renderedHtmlBody, String renderedTextBody,
                          String eventPayloadJson, EmailDeliveryStatus status, String failureReason,
                          Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.ruleId = ruleId;
        this.templateId = templateId;
        this.templateVersionId = templateVersionId;
        this.eventDefinitionId = eventDefinitionId;
        this.workspaceId = workspaceId;
        this.toEmail = toEmail;
        this.renderedSubject = renderedSubject;
        this.renderedHtmlBody = renderedHtmlBody;
        this.renderedTextBody = renderedTextBody;
        this.eventPayloadJson = eventPayloadJson;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EmailDelivery create(UUID ruleId, UUID templateId, UUID templateVersionId,
                                       UUID eventDefinitionId, UUID workspaceId,
                                       String toEmail, String renderedSubject,
                                       String renderedHtmlBody, String renderedTextBody,
                                       String eventPayloadJson) {
        Instant now = Instant.now();
        return new EmailDelivery(UUID.randomUUID(), ruleId, templateId, templateVersionId,
                eventDefinitionId, workspaceId, toEmail, renderedSubject,
                renderedHtmlBody, renderedTextBody, eventPayloadJson,
                EmailDeliveryStatus.CREATED, null, now, now);
    }

    public static EmailDelivery reconstitute(UUID id, UUID ruleId, UUID templateId, UUID templateVersionId,
                                              UUID eventDefinitionId, UUID workspaceId,
                                              String toEmail, String renderedSubject,
                                              String renderedHtmlBody, String renderedTextBody,
                                              String eventPayloadJson, EmailDeliveryStatus status,
                                              String failureReason, Instant createdAt, Instant updatedAt) {
        return new EmailDelivery(id, ruleId, templateId, templateVersionId,
                eventDefinitionId, workspaceId, toEmail, renderedSubject,
                renderedHtmlBody, renderedTextBody, eventPayloadJson,
                status, failureReason, createdAt, updatedAt);
    }

    public static EmailDelivery createSkipped(UUID ruleId, UUID templateId, UUID templateVersionId,
                                               UUID eventDefinitionId, UUID workspaceId,
                                               String skipReason, String eventPayloadJson) {
        Instant now = Instant.now();
        EmailDelivery delivery = new EmailDelivery(UUID.randomUUID(), ruleId, templateId, templateVersionId,
                eventDefinitionId, workspaceId, null, null, null, null,
                eventPayloadJson, EmailDeliveryStatus.SKIPPED, skipReason, now, now);
        return delivery;
    }

    public void markSent() {
        this.status = EmailDeliveryStatus.SENT;
        this.updatedAt = Instant.now();
    }

    public void markFailed(String reason) {
        this.status = EmailDeliveryStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
    }

    public void markSkipped(String reason) {
        this.status = EmailDeliveryStatus.SKIPPED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
    }

    public void markCancelled() {
        this.status = EmailDeliveryStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public UUID id() { return id; }
    public UUID ruleId() { return ruleId; }
    public UUID templateId() { return templateId; }
    public UUID templateVersionId() { return templateVersionId; }
    public UUID eventDefinitionId() { return eventDefinitionId; }
    public UUID workspaceId() { return workspaceId; }
    public String toEmail() { return toEmail; }
    public String renderedSubject() { return renderedSubject; }
    public String renderedHtmlBody() { return renderedHtmlBody; }
    public String renderedTextBody() { return renderedTextBody; }
    public String eventPayloadJson() { return eventPayloadJson; }
    public EmailDeliveryStatus status() { return status; }
    public String failureReason() { return failureReason; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}

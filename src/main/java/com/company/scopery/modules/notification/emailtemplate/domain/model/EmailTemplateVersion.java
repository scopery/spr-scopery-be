package com.company.scopery.modules.notification.emailtemplate.domain.model;

import com.company.scopery.modules.notification.emailtemplate.domain.enums.EmailTemplateVersionStatus;

import java.time.Instant;
import java.util.UUID;

public class EmailTemplateVersion {

    private final UUID id;
    private final UUID templateId;
    private final int versionNumber;
    private String subjectTemplate;
    private String htmlBodyTemplate;
    private String textBodyTemplate;
    private EmailTemplateVersionStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private EmailTemplateVersion(UUID id, UUID templateId, int versionNumber,
                                 String subjectTemplate, String htmlBodyTemplate, String textBodyTemplate,
                                 EmailTemplateVersionStatus status,
                                 Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.templateId = templateId;
        this.versionNumber = versionNumber;
        this.subjectTemplate = subjectTemplate;
        this.htmlBodyTemplate = htmlBodyTemplate;
        this.textBodyTemplate = textBodyTemplate;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static EmailTemplateVersion createDraft(UUID templateId, int versionNumber,
                                                   String subjectTemplate, String htmlBodyTemplate,
                                                   String textBodyTemplate) {
        validateSubject(subjectTemplate);
        validateBody(htmlBodyTemplate);
        Instant now = Instant.now();
        return new EmailTemplateVersion(UUID.randomUUID(), templateId, versionNumber,
                subjectTemplate, htmlBodyTemplate, textBodyTemplate,
                EmailTemplateVersionStatus.DRAFT, now, now);
    }

    public static EmailTemplateVersion reconstitute(UUID id, UUID templateId, int versionNumber,
                                                    String subjectTemplate, String htmlBodyTemplate,
                                                    String textBodyTemplate,
                                                    EmailTemplateVersionStatus status,
                                                    Instant createdAt, Instant updatedAt) {
        return new EmailTemplateVersion(id, templateId, versionNumber,
                subjectTemplate, htmlBodyTemplate, textBodyTemplate, status, createdAt, updatedAt);
    }

    public void updateContent(String subjectTemplate, String htmlBodyTemplate, String textBodyTemplate) {
        if (this.status != EmailTemplateVersionStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT versions can be edited");
        }
        validateSubject(subjectTemplate);
        validateBody(htmlBodyTemplate);
        this.subjectTemplate = subjectTemplate;
        this.htmlBodyTemplate = htmlBodyTemplate;
        this.textBodyTemplate = textBodyTemplate;
        this.updatedAt = Instant.now();
    }

    public void publish() {
        if (this.status == EmailTemplateVersionStatus.ARCHIVED) {
            throw new IllegalStateException("Archived version cannot be published");
        }
        this.status = EmailTemplateVersionStatus.PUBLISHED;
        this.updatedAt = Instant.now();
    }

    public void archive() {
        this.status = EmailTemplateVersionStatus.ARCHIVED;
        this.updatedAt = Instant.now();
    }

    private static void validateSubject(String subject) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Email subject template must not be blank");
        }
    }

    private static void validateBody(String body) {
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Email HTML body template must not be blank");
        }
    }

    public UUID id() { return id; }
    public UUID templateId() { return templateId; }
    public int versionNumber() { return versionNumber; }
    public String subjectTemplate() { return subjectTemplate; }
    public String htmlBodyTemplate() { return htmlBodyTemplate; }
    public String textBodyTemplate() { return textBodyTemplate; }
    public EmailTemplateVersionStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}

package com.company.scopery.modules.notification.emailtemplate.domain;

import java.time.Instant;
import java.util.UUID;

public class EmailTemplate {

    private final UUID id;
    private final EmailTemplateCode code;
    private String name;
    private String description;
    private final EmailTemplateScope scope;
    private final UUID workspaceId;
    private final UUID eventDefinitionId;
    private EmailTemplateStatus status;
    private UUID currentVersionId;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private EmailTemplate(UUID id, EmailTemplateCode code, String name, String description,
                          EmailTemplateScope scope, UUID workspaceId, UUID eventDefinitionId,
                          EmailTemplateStatus status, UUID currentVersionId,
                          Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.scope = scope;
        this.workspaceId = workspaceId;
        this.eventDefinitionId = eventDefinitionId;
        this.status = status;
        this.currentVersionId = currentVersionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static EmailTemplate createSystem(EmailTemplateCode code, String name, String description,
                                             UUID eventDefinitionId) {
        validateName(name);
        Instant now = Instant.now();
        return new EmailTemplate(UUID.randomUUID(), code, name, description,
                EmailTemplateScope.SYSTEM, null, eventDefinitionId,
                EmailTemplateStatus.DRAFT, null, now, now, null);
    }

    public static EmailTemplate createWorkspace(EmailTemplateCode code, String name, String description,
                                                UUID workspaceId, UUID eventDefinitionId) {
        validateName(name);
        if (workspaceId == null) throw new IllegalArgumentException("workspaceId required for WORKSPACE scope");
        Instant now = Instant.now();
        return new EmailTemplate(UUID.randomUUID(), code, name, description,
                EmailTemplateScope.WORKSPACE, workspaceId, eventDefinitionId,
                EmailTemplateStatus.DRAFT, null, now, now, null);
    }

    public static EmailTemplate reconstitute(UUID id, EmailTemplateCode code, String name, String description,
                                             EmailTemplateScope scope, UUID workspaceId,
                                             UUID eventDefinitionId, EmailTemplateStatus status,
                                             UUID currentVersionId,
                                             Instant createdAt, Instant updatedAt, Instant deletedAt) {
        return new EmailTemplate(id, code, name, description, scope, workspaceId, eventDefinitionId,
                status, currentVersionId, createdAt, updatedAt, deletedAt);
    }

    public void update(String name, String description) {
        validateName(name);
        if (this.status == EmailTemplateStatus.DELETED) {
            throw new IllegalStateException("Cannot update a deleted email template");
        }
        this.name = name;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void publishVersion(UUID versionId) {
        if (this.status == EmailTemplateStatus.DELETED) {
            throw new IllegalStateException("Cannot publish version on a deleted email template");
        }
        this.currentVersionId = versionId;
        if (this.status == EmailTemplateStatus.DRAFT) {
            this.status = EmailTemplateStatus.ACTIVE;
        }
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == EmailTemplateStatus.DELETED) {
            throw new IllegalStateException("Deleted email template cannot be activated");
        }
        this.status = EmailTemplateStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        if (this.status == EmailTemplateStatus.DELETED) {
            throw new IllegalStateException("Deleted email template cannot be deactivated");
        }
        this.status = EmailTemplateStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void softDelete() {
        this.status = EmailTemplateStatus.DELETED;
        this.deletedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Email template name must not be blank");
        }
    }

    public UUID id() { return id; }
    public EmailTemplateCode code() { return code; }
    public String name() { return name; }
    public String description() { return description; }
    public EmailTemplateScope scope() { return scope; }
    public UUID workspaceId() { return workspaceId; }
    public UUID eventDefinitionId() { return eventDefinitionId; }
    public EmailTemplateStatus status() { return status; }
    public UUID currentVersionId() { return currentVersionId; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Instant deletedAt() { return deletedAt; }
}

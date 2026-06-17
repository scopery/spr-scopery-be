package com.company.scopery.modules.notification.emailrule.domain;

import java.time.Instant;
import java.util.UUID;

public class EmailRule {

    private final UUID id;
    private final String code;
    private String name;
    private String description;
    private final EmailRuleScope scope;
    private final UUID workspaceId;
    private final UUID eventDefinitionId;
    private final UUID templateId;
    private EmailRecipientStrategy recipientStrategy;
    private String recipientConfigJson;
    private int priority;
    private boolean enabled;
    private EmailRuleStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    private EmailRule(UUID id, String code, String name, String description,
                      EmailRuleScope scope, UUID workspaceId,
                      UUID eventDefinitionId, UUID templateId,
                      EmailRecipientStrategy recipientStrategy, String recipientConfigJson,
                      int priority, boolean enabled, EmailRuleStatus status,
                      Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.scope = scope;
        this.workspaceId = workspaceId;
        this.eventDefinitionId = eventDefinitionId;
        this.templateId = templateId;
        this.recipientStrategy = recipientStrategy;
        this.recipientConfigJson = recipientConfigJson;
        this.priority = priority;
        this.enabled = enabled;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public static EmailRule createSystem(String code, String name, String description,
                                         UUID eventDefinitionId, UUID templateId,
                                         EmailRecipientStrategy recipientStrategy,
                                         String recipientConfigJson, int priority) {
        validateCode(code);
        validateName(name);
        Instant now = Instant.now();
        return new EmailRule(UUID.randomUUID(), code.trim().toUpperCase(), name, description,
                EmailRuleScope.SYSTEM, null, eventDefinitionId, templateId,
                recipientStrategy, recipientConfigJson, priority, true,
                EmailRuleStatus.ACTIVE, now, now, null);
    }

    public static EmailRule createWorkspace(String code, String name, String description,
                                             UUID workspaceId, UUID eventDefinitionId, UUID templateId,
                                             EmailRecipientStrategy recipientStrategy,
                                             String recipientConfigJson, int priority) {
        validateCode(code);
        validateName(name);
        if (workspaceId == null) throw new IllegalArgumentException("workspaceId required for WORKSPACE scope");
        Instant now = Instant.now();
        return new EmailRule(UUID.randomUUID(), code.trim().toUpperCase(), name, description,
                EmailRuleScope.WORKSPACE, workspaceId, eventDefinitionId, templateId,
                recipientStrategy, recipientConfigJson, priority, true,
                EmailRuleStatus.ACTIVE, now, now, null);
    }

    public static EmailRule reconstitute(UUID id, String code, String name, String description,
                                          EmailRuleScope scope, UUID workspaceId,
                                          UUID eventDefinitionId, UUID templateId,
                                          EmailRecipientStrategy recipientStrategy,
                                          String recipientConfigJson, int priority, boolean enabled,
                                          EmailRuleStatus status,
                                          Instant createdAt, Instant updatedAt, Instant deletedAt) {
        return new EmailRule(id, code, name, description, scope, workspaceId,
                eventDefinitionId, templateId, recipientStrategy, recipientConfigJson,
                priority, enabled, status, createdAt, updatedAt, deletedAt);
    }

    public void update(String name, String description, EmailRecipientStrategy recipientStrategy,
                       String recipientConfigJson, int priority) {
        validateName(name);
        if (this.status == EmailRuleStatus.DELETED) {
            throw new IllegalStateException("Cannot update a deleted email rule");
        }
        this.name = name;
        this.description = description;
        this.recipientStrategy = recipientStrategy;
        this.recipientConfigJson = recipientConfigJson;
        this.priority = priority;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == EmailRuleStatus.DELETED) {
            throw new IllegalStateException("Deleted email rule cannot be activated");
        }
        this.status = EmailRuleStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        if (this.status == EmailRuleStatus.DELETED) {
            throw new IllegalStateException("Deleted email rule cannot be deactivated");
        }
        this.status = EmailRuleStatus.INACTIVE;
        this.updatedAt = Instant.now();
    }

    public void enable() {
        this.enabled = true;
        this.updatedAt = Instant.now();
    }

    public void disable() {
        this.enabled = false;
        this.updatedAt = Instant.now();
    }

    public void softDelete() {
        this.status = EmailRuleStatus.DELETED;
        this.enabled = false;
        this.deletedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    private static void validateCode(String code) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Rule code must not be blank");
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Rule name must not be blank");
    }

    public UUID id() { return id; }
    public String code() { return code; }
    public String name() { return name; }
    public String description() { return description; }
    public EmailRuleScope scope() { return scope; }
    public UUID workspaceId() { return workspaceId; }
    public UUID eventDefinitionId() { return eventDefinitionId; }
    public UUID templateId() { return templateId; }
    public EmailRecipientStrategy recipientStrategy() { return recipientStrategy; }
    public String recipientConfigJson() { return recipientConfigJson; }
    public int priority() { return priority; }
    public boolean enabled() { return enabled; }
    public EmailRuleStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Instant deletedAt() { return deletedAt; }
}

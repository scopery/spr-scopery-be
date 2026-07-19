package com.company.scopery.modules.aiagent.prompt.domain.model;

import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptContentFormat;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PromptVersion {

    private final UUID id;
    private final UUID templateId;
    private final int versionNumber;
    private String title;
    private String content;
    private String systemPrompt;
    private String userPromptTemplate;
    private PromptContentFormat contentFormat;
    private String variableSchema;
    private String changeNote;
    private String responseFormat;
    private String responseSchemaJson;
    private BigDecimal temperature;
    private BigDecimal topP;
    private Integer maxTokens;
    private Instant activatedAt;
    private String activatedBy;
    private PromptVersionStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private PromptVersion(UUID id, UUID templateId, int versionNumber, String title,
                          String content, String systemPrompt, String userPromptTemplate,
                          PromptContentFormat contentFormat,
                          String variableSchema, String changeNote,
                          String responseFormat, String responseSchemaJson,
                          BigDecimal temperature, BigDecimal topP, Integer maxTokens,
                          Instant activatedAt, String activatedBy,
                          PromptVersionStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.templateId = templateId;
        this.versionNumber = versionNumber;
        this.title = title;
        this.content = content;
        this.systemPrompt = systemPrompt;
        this.userPromptTemplate = userPromptTemplate;
        this.contentFormat = contentFormat;
        this.variableSchema = variableSchema;
        this.changeNote = changeNote;
        this.responseFormat = responseFormat;
        this.responseSchemaJson = responseSchemaJson;
        this.temperature = temperature;
        this.topP = topP;
        this.maxTokens = maxTokens;
        this.activatedAt = activatedAt;
        this.activatedBy = activatedBy;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PromptVersion create(UUID templateId, int versionNumber, String title,
                                       String content, PromptContentFormat contentFormat,
                                       String variableSchema, String changeNote) {
        return create(templateId, versionNumber, title, content, contentFormat, variableSchema, changeNote,
                null, content, null, null, null, null, null);
    }

    public static PromptVersion create(UUID templateId, int versionNumber, String title,
                                       String content, PromptContentFormat contentFormat,
                                       String variableSchema, String changeNote,
                                       String systemPrompt, String userPromptTemplate,
                                       String responseFormat, String responseSchemaJson,
                                       BigDecimal temperature, BigDecimal topP, Integer maxTokens) {
        String resolvedContent = content != null && !content.isBlank()
                ? content
                : (userPromptTemplate != null ? userPromptTemplate : "");
        validateContent(resolvedContent);
        Instant now = Instant.now();
        String userTpl = userPromptTemplate != null && !userPromptTemplate.isBlank()
                ? userPromptTemplate : resolvedContent;
        return new PromptVersion(UUID.randomUUID(), templateId, versionNumber, title,
                resolvedContent, systemPrompt, userTpl, contentFormat, variableSchema, changeNote,
                responseFormat, responseSchemaJson, temperature, topP, maxTokens,
                null, null, PromptVersionStatus.DRAFT, now, now);
    }

    public static PromptVersion reconstitute(UUID id, UUID templateId, int versionNumber,
                                             String title, String content,
                                             PromptContentFormat contentFormat,
                                             String variableSchema, String changeNote,
                                             PromptVersionStatus status,
                                             Instant createdAt, Instant updatedAt) {
        return reconstitute(id, templateId, versionNumber, title, content, null, content,
                contentFormat, variableSchema, changeNote, null, null, null, null, null,
                null, null, status, createdAt, updatedAt);
    }

    public static PromptVersion reconstitute(UUID id, UUID templateId, int versionNumber,
                                             String title, String content,
                                             String systemPrompt, String userPromptTemplate,
                                             PromptContentFormat contentFormat,
                                             String variableSchema, String changeNote,
                                             String responseFormat, String responseSchemaJson,
                                             BigDecimal temperature, BigDecimal topP, Integer maxTokens,
                                             Instant activatedAt, String activatedBy,
                                             PromptVersionStatus status,
                                             Instant createdAt, Instant updatedAt) {
        return new PromptVersion(id, templateId, versionNumber, title, content,
                systemPrompt, userPromptTemplate, contentFormat,
                variableSchema, changeNote, responseFormat, responseSchemaJson,
                temperature, topP, maxTokens, activatedAt, activatedBy,
                status, createdAt, updatedAt);
    }

    public void update(String title, String content, PromptContentFormat contentFormat,
                       String variableSchema, String changeNote) {
        update(title, content, contentFormat, variableSchema, changeNote,
                this.systemPrompt, this.userPromptTemplate, this.responseFormat,
                this.responseSchemaJson, this.temperature, this.topP, this.maxTokens);
    }

    public void update(String title, String content, PromptContentFormat contentFormat,
                       String variableSchema, String changeNote,
                       String systemPrompt, String userPromptTemplate,
                       String responseFormat, String responseSchemaJson,
                       BigDecimal temperature, BigDecimal topP, Integer maxTokens) {
        if (this.status != PromptVersionStatus.DRAFT) {
            throw new IllegalStateException(
                    "Prompt version content can only be edited while in DRAFT status, current: " + this.status);
        }
        String resolvedContent = content != null && !content.isBlank()
                ? content
                : (userPromptTemplate != null ? userPromptTemplate : this.content);
        validateContent(resolvedContent);
        this.title = title;
        this.content = resolvedContent;
        this.contentFormat = contentFormat;
        this.variableSchema = variableSchema;
        this.changeNote = changeNote;
        this.systemPrompt = systemPrompt;
        this.userPromptTemplate = userPromptTemplate != null && !userPromptTemplate.isBlank()
                ? userPromptTemplate : resolvedContent;
        this.responseFormat = responseFormat;
        this.responseSchemaJson = responseSchemaJson;
        this.temperature = temperature;
        this.topP = topP;
        this.maxTokens = maxTokens;
        this.updatedAt = Instant.now();
    }

    /**
     * Prefer userPromptTemplate when set; otherwise fall back to legacy content field.
     */
    public String resolvedPromptContent() {
        if (userPromptTemplate != null && !userPromptTemplate.isBlank()) {
            return userPromptTemplate;
        }
        return content;
    }

    public void activate(String activatedBy) {
        if (this.status == PromptVersionStatus.ARCHIVED) {
            throw new IllegalStateException("Archived prompt version cannot be activated again");
        }
        this.status = PromptVersionStatus.ACTIVE;
        this.activatedAt = Instant.now();
        this.activatedBy = activatedBy != null && !activatedBy.isBlank() ? activatedBy : "SYSTEM";
        this.updatedAt = Instant.now();
    }

    /** @deprecated use {@link #activate(String)} */
    public void activate() {
        activate("SYSTEM");
    }

    public void archive() {
        if (this.status == PromptVersionStatus.ARCHIVED) {
            throw new IllegalStateException("Prompt version is already archived");
        }
        this.status = PromptVersionStatus.ARCHIVED;
        this.updatedAt = Instant.now();
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Prompt version content is required");
        }
    }

    public UUID id() { return id; }
    public UUID templateId() { return templateId; }
    public int versionNumber() { return versionNumber; }
    public String title() { return title; }
    public String content() { return content; }
    public String systemPrompt() { return systemPrompt; }
    public String userPromptTemplate() { return userPromptTemplate; }
    public PromptContentFormat contentFormat() { return contentFormat; }
    public String variableSchema() { return variableSchema; }
    public String changeNote() { return changeNote; }
    public String responseFormat() { return responseFormat; }
    public String responseSchemaJson() { return responseSchemaJson; }
    public BigDecimal temperature() { return temperature; }
    public BigDecimal topP() { return topP; }
    public Integer maxTokens() { return maxTokens; }
    public Instant activatedAt() { return activatedAt; }
    public String activatedBy() { return activatedBy; }
    public PromptVersionStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}

package com.company.scopery.modules.aiagent.prompt.domain.model;

import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptContentFormat;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;

import java.time.Instant;
import java.util.UUID;

public class PromptVersion {

    private final UUID id;
    private final UUID templateId;
    private final int versionNumber;
    private String title;
    private String content;
    private PromptContentFormat contentFormat;
    private String variableSchema;
    private String changeNote;
    private PromptVersionStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private PromptVersion(UUID id, UUID templateId, int versionNumber, String title,
                          String content, PromptContentFormat contentFormat,
                          String variableSchema, String changeNote,
                          PromptVersionStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.templateId = templateId;
        this.versionNumber = versionNumber;
        this.title = title;
        this.content = content;
        this.contentFormat = contentFormat;
        this.variableSchema = variableSchema;
        this.changeNote = changeNote;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PromptVersion create(UUID templateId, int versionNumber, String title,
                                       String content, PromptContentFormat contentFormat,
                                       String variableSchema, String changeNote) {
        validateContent(content);
        Instant now = Instant.now();
        return new PromptVersion(UUID.randomUUID(), templateId, versionNumber, title,
                content, contentFormat, variableSchema, changeNote,
                PromptVersionStatus.DRAFT, now, now);
    }

    public static PromptVersion reconstitute(UUID id, UUID templateId, int versionNumber,
                                             String title, String content,
                                             PromptContentFormat contentFormat,
                                             String variableSchema, String changeNote,
                                             PromptVersionStatus status,
                                             Instant createdAt, Instant updatedAt) {
        return new PromptVersion(id, templateId, versionNumber, title, content, contentFormat,
                variableSchema, changeNote, status, createdAt, updatedAt);
    }

    public void update(String title, String content, PromptContentFormat contentFormat,
                       String variableSchema, String changeNote) {
        if (this.status != PromptVersionStatus.DRAFT) {
            throw new IllegalStateException(
                    "Prompt version content can only be edited while in DRAFT status, current: " + this.status);
        }
        validateContent(content);
        this.title = title;
        this.content = content;
        this.contentFormat = contentFormat;
        this.variableSchema = variableSchema;
        this.changeNote = changeNote;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == PromptVersionStatus.ARCHIVED) {
            throw new IllegalStateException("Archived prompt version cannot be activated again");
        }
        this.status = PromptVersionStatus.ACTIVE;
        this.updatedAt = Instant.now();
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
    public PromptContentFormat contentFormat() { return contentFormat; }
    public String variableSchema() { return variableSchema; }
    public String changeNote() { return changeNote; }
    public PromptVersionStatus status() { return status; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
}

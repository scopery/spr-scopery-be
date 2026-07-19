package com.company.scopery.modules.aiassistant.domain.model;

import java.time.Instant;
import java.util.UUID;

public class AiGuideDefinition {

    private final UUID id;
    private final String code;
    private final String pageCode;
    private String fieldCode;
    private String actionCode;
    private final String locale;
    private String title;
    private String bodyMarkdown;
    private int metadataVersion;
    private String sourceKind;
    private String status;
    private final Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private long version;

    private AiGuideDefinition(UUID id, String code, String pageCode, String fieldCode,
                               String actionCode, String locale, String title, String bodyMarkdown,
                               int metadataVersion, String sourceKind, String status,
                               Instant createdAt, UUID createdBy, Instant updatedAt, UUID updatedBy, long version) {
        this.id = id;
        this.code = code;
        this.pageCode = pageCode;
        this.fieldCode = fieldCode;
        this.actionCode = actionCode;
        this.locale = locale;
        this.title = title;
        this.bodyMarkdown = bodyMarkdown;
        this.metadataVersion = metadataVersion;
        this.sourceKind = sourceKind;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public static AiGuideDefinition reconstitute(UUID id, String code, String pageCode, String fieldCode,
                                                  String actionCode, String locale, String title,
                                                  String bodyMarkdown, int metadataVersion, String sourceKind,
                                                  String status, Instant createdAt, UUID createdBy,
                                                  Instant updatedAt, UUID updatedBy, long version) {
        return new AiGuideDefinition(id, code, pageCode, fieldCode, actionCode, locale, title,
                bodyMarkdown, metadataVersion, sourceKind, status, createdAt, createdBy, updatedAt, updatedBy, version);
    }

    public UUID id() { return id; }
    public String code() { return code; }
    public String pageCode() { return pageCode; }
    public String fieldCode() { return fieldCode; }
    public String actionCode() { return actionCode; }
    public String locale() { return locale; }
    public String title() { return title; }
    public String bodyMarkdown() { return bodyMarkdown; }
    public int metadataVersion() { return metadataVersion; }
    public String sourceKind() { return sourceKind; }
    public String status() { return status; }
    public Instant createdAt() { return createdAt; }
    public UUID createdBy() { return createdBy; }
    public Instant updatedAt() { return updatedAt; }
    public UUID updatedBy() { return updatedBy; }
    public long version() { return version; }
}

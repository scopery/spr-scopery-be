package com.company.scopery.modules.aiassistant.domain.model;

import java.time.Instant;
import java.util.UUID;

public class AiSuggestedQuestion {

    private final UUID id;
    private final String code;
    private final String pageCode;
    private String entityType;
    private String actionCode;
    private final String locale;
    private String questionText;
    private int displayOrder;
    private String status;
    private final Instant createdAt;
    private UUID createdBy;
    private Instant updatedAt;
    private UUID updatedBy;
    private long version;

    private AiSuggestedQuestion(UUID id, String code, String pageCode, String entityType,
                                 String actionCode, String locale, String questionText,
                                 int displayOrder, String status,
                                 Instant createdAt, UUID createdBy, Instant updatedAt, UUID updatedBy, long version) {
        this.id = id;
        this.code = code;
        this.pageCode = pageCode;
        this.entityType = entityType;
        this.actionCode = actionCode;
        this.locale = locale;
        this.questionText = questionText;
        this.displayOrder = displayOrder;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public static AiSuggestedQuestion reconstitute(UUID id, String code, String pageCode, String entityType,
                                                    String actionCode, String locale, String questionText,
                                                    int displayOrder, String status,
                                                    Instant createdAt, UUID createdBy,
                                                    Instant updatedAt, UUID updatedBy, long version) {
        return new AiSuggestedQuestion(id, code, pageCode, entityType, actionCode, locale, questionText,
                displayOrder, status, createdAt, createdBy, updatedAt, updatedBy, version);
    }

    public UUID id() { return id; }
    public String code() { return code; }
    public String pageCode() { return pageCode; }
    public String entityType() { return entityType; }
    public String actionCode() { return actionCode; }
    public String locale() { return locale; }
    public String questionText() { return questionText; }
    public int displayOrder() { return displayOrder; }
    public String status() { return status; }
    public Instant createdAt() { return createdAt; }
    public UUID createdBy() { return createdBy; }
    public Instant updatedAt() { return updatedAt; }
    public UUID updatedBy() { return updatedBy; }
    public long version() { return version; }
}

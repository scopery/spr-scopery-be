package com.company.scopery.modules.specpack.outline.domain.model;

import com.company.scopery.modules.specpack.outline.domain.enums.OutlineStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class SpecPackOutline {

    private UUID id;
    private UUID sessionId;
    private int versionNumber;
    private Map<String, Object> outlineJson;
    private OutlineStatus status;
    private Instant approvedAt;
    private String createdBy;
    private Instant createdAt;

    private SpecPackOutline() {}

    public static SpecPackOutline create(UUID sessionId, int versionNumber, Map<String, Object> outlineJson) {
        SpecPackOutline o = new SpecPackOutline();
        o.id = UUID.randomUUID();
        o.sessionId = sessionId;
        o.versionNumber = versionNumber;
        o.outlineJson = outlineJson;
        o.status = OutlineStatus.DRAFT;
        o.createdAt = Instant.now();
        return o;
    }

    public static SpecPackOutline reconstitute(UUID id, UUID sessionId, int versionNumber,
                                               Map<String, Object> outlineJson, OutlineStatus status,
                                               Instant approvedAt, String createdBy, Instant createdAt) {
        SpecPackOutline o = new SpecPackOutline();
        o.id = id;
        o.sessionId = sessionId;
        o.versionNumber = versionNumber;
        o.outlineJson = outlineJson;
        o.status = status;
        o.approvedAt = approvedAt;
        o.createdBy = createdBy;
        o.createdAt = createdAt;
        return o;
    }

    public void approve() {
        if (status != OutlineStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT outlines can be approved; current status: " + status);
        }
        this.status = OutlineStatus.APPROVED;
        this.approvedAt = Instant.now();
    }

    public void supersede() {
        this.status = OutlineStatus.SUPERSEDED;
    }

    public UUID id()                        { return id; }
    public UUID sessionId()                 { return sessionId; }
    public int versionNumber()              { return versionNumber; }
    public Map<String, Object> outlineJson() { return outlineJson; }
    public OutlineStatus status()           { return status; }
    public Instant approvedAt()             { return approvedAt; }
    public String createdBy()               { return createdBy; }
    public Instant createdAt()              { return createdAt; }
}

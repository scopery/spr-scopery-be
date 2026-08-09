package com.company.scopery.modules.elicitation.session.domain.model;

import com.company.scopery.modules.elicitation.session.domain.enums.SessionStatus;

import java.time.Instant;
import java.util.UUID;

public class ElicitationSession {

    private UUID id;
    private UUID projectId;
    private UUID scopePackageId;
    private String title;
    private SessionStatus status;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    private ElicitationSession() {}

    public static ElicitationSession create(UUID projectId, UUID scopePackageId, String title) {
        ElicitationSession s = new ElicitationSession();
        s.id = UUID.randomUUID();
        s.projectId = projectId;
        s.scopePackageId = scopePackageId;
        s.title = title;
        s.status = SessionStatus.ACTIVE;
        s.createdAt = Instant.now();
        s.updatedAt = s.createdAt;
        return s;
    }

    public static ElicitationSession reconstitute(UUID id, UUID projectId, UUID scopePackageId, String title,
                                                   SessionStatus status, String createdBy, String updatedBy,
                                                   Instant createdAt, Instant updatedAt) {
        ElicitationSession s = new ElicitationSession();
        s.id = id;
        s.projectId = projectId;
        s.scopePackageId = scopePackageId;
        s.title = title;
        s.status = status;
        s.createdBy = createdBy;
        s.updatedBy = updatedBy;
        s.createdAt = createdAt;
        s.updatedAt = updatedAt;
        return s;
    }

    public void close() {
        if (status != SessionStatus.ACTIVE) throw new IllegalStateException("Session is not active");
        this.status = SessionStatus.CLOSED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (status != SessionStatus.ACTIVE) throw new IllegalStateException("Session is not active");
        this.status = SessionStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public UUID id()               { return id; }
    public UUID projectId()        { return projectId; }
    public UUID scopePackageId()   { return scopePackageId; }
    public String title()          { return title; }
    public SessionStatus status()  { return status; }
    public String createdBy()      { return createdBy; }
    public String updatedBy()      { return updatedBy; }
    public Instant createdAt()     { return createdAt; }
    public Instant updatedAt()     { return updatedAt; }
}

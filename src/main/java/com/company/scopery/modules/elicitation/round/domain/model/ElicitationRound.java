package com.company.scopery.modules.elicitation.round.domain.model;

import com.company.scopery.modules.elicitation.question.domain.enums.ClarityLevel;
import com.company.scopery.modules.elicitation.round.domain.enums.RoundStatus;

import java.time.Instant;
import java.util.UUID;

public class ElicitationRound {

    private UUID id;
    private UUID sessionId;
    private int roundNumber;
    private String questionsJson;
    private ClarityLevel overallClarity;
    private RoundStatus status;
    private String scopeSnapshotJson;
    private Boolean shouldContinue;
    private Instant evaluatedAt;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    private ElicitationRound() {}

    public static ElicitationRound create(UUID sessionId, int roundNumber, String scopeSnapshotJson) {
        ElicitationRound r = new ElicitationRound();
        r.id = UUID.randomUUID();
        r.sessionId = sessionId;
        r.roundNumber = roundNumber;
        r.scopeSnapshotJson = scopeSnapshotJson;
        r.questionsJson = "[]";
        r.status = RoundStatus.ACTIVE;
        r.createdAt = Instant.now();
        r.updatedAt = r.createdAt;
        return r;
    }

    public static ElicitationRound reconstitute(UUID id, UUID sessionId, int roundNumber, String questionsJson,
                                                 ClarityLevel overallClarity, RoundStatus status,
                                                 String scopeSnapshotJson, Boolean shouldContinue,
                                                 Instant evaluatedAt, String createdBy, String updatedBy,
                                                 Instant createdAt, Instant updatedAt) {
        ElicitationRound r = new ElicitationRound();
        r.id = id;
        r.sessionId = sessionId;
        r.roundNumber = roundNumber;
        r.questionsJson = questionsJson;
        r.overallClarity = overallClarity;
        r.status = status;
        r.scopeSnapshotJson = scopeSnapshotJson;
        r.shouldContinue = shouldContinue;
        r.evaluatedAt = evaluatedAt;
        r.createdBy = createdBy;
        r.updatedBy = updatedBy;
        r.createdAt = createdAt;
        r.updatedAt = updatedAt;
        return r;
    }

    public void markEvaluated(String questionsJson, ClarityLevel overallClarity, boolean shouldContinue) {
        if (status != RoundStatus.ACTIVE) throw new IllegalStateException("Round is not ACTIVE");
        this.questionsJson = questionsJson;
        this.overallClarity = overallClarity;
        this.shouldContinue = shouldContinue;
        this.status = RoundStatus.EVALUATED;
        this.evaluatedAt = Instant.now();
        this.updatedAt = this.evaluatedAt;
    }

    public UUID id()                     { return id; }
    public UUID sessionId()              { return sessionId; }
    public int roundNumber()             { return roundNumber; }
    public String questionsJson()        { return questionsJson; }
    public ClarityLevel overallClarity() { return overallClarity; }
    public RoundStatus status()          { return status; }
    public String scopeSnapshotJson()    { return scopeSnapshotJson; }
    public Boolean shouldContinue()      { return shouldContinue; }
    public Instant evaluatedAt()         { return evaluatedAt; }
    public String createdBy()            { return createdBy; }
    public String updatedBy()            { return updatedBy; }
    public Instant createdAt()           { return createdAt; }
    public Instant updatedAt()           { return updatedAt; }
}

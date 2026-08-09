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
    private Instant submittedAt;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    private ElicitationRound() {}

    public static ElicitationRound create(UUID sessionId, int roundNumber, String questionsJson,
                                           ClarityLevel overallClarity) {
        ElicitationRound r = new ElicitationRound();
        r.id = UUID.randomUUID();
        r.sessionId = sessionId;
        r.roundNumber = roundNumber;
        r.questionsJson = questionsJson;
        r.overallClarity = overallClarity;
        r.status = RoundStatus.DRAFT;
        r.createdAt = Instant.now();
        r.updatedAt = r.createdAt;
        return r;
    }

    public static ElicitationRound reconstitute(UUID id, UUID sessionId, int roundNumber, String questionsJson,
                                                 ClarityLevel overallClarity, RoundStatus status,
                                                 Instant submittedAt, String createdBy, String updatedBy,
                                                 Instant createdAt, Instant updatedAt) {
        ElicitationRound r = new ElicitationRound();
        r.id = id;
        r.sessionId = sessionId;
        r.roundNumber = roundNumber;
        r.questionsJson = questionsJson;
        r.overallClarity = overallClarity;
        r.status = status;
        r.submittedAt = submittedAt;
        r.createdBy = createdBy;
        r.updatedBy = updatedBy;
        r.createdAt = createdAt;
        r.updatedAt = updatedAt;
        return r;
    }

    public void submit() {
        if (status != RoundStatus.DRAFT) throw new IllegalStateException("Round is not in DRAFT status");
        this.status = RoundStatus.SUBMITTED;
        this.submittedAt = Instant.now();
        this.updatedAt = this.submittedAt;
    }

    public void markProcessing() {
        this.status = RoundStatus.PROCESSING;
        this.updatedAt = Instant.now();
    }

    public void markCompleted() {
        this.status = RoundStatus.COMPLETED;
        this.updatedAt = Instant.now();
    }

    public UUID id()                     { return id; }
    public UUID sessionId()              { return sessionId; }
    public int roundNumber()             { return roundNumber; }
    public String questionsJson()        { return questionsJson; }
    public ClarityLevel overallClarity() { return overallClarity; }
    public RoundStatus status()          { return status; }
    public Instant submittedAt()         { return submittedAt; }
    public String createdBy()            { return createdBy; }
    public String updatedBy()            { return updatedBy; }
    public Instant createdAt()           { return createdAt; }
    public Instant updatedAt()           { return updatedAt; }
}

package com.company.scopery.modules.elicitation.suggestion.domain.model;

import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionStatus;

import java.time.Instant;
import java.util.UUID;

public class ElicitationSuggestion {

    private UUID id;
    private UUID roundId;
    private String overallSummary;
    private SuggestionStatus status;
    private String aiRawResponse;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    private ElicitationSuggestion() {}

    public static ElicitationSuggestion create(UUID roundId, String overallSummary, String aiRawResponse) {
        ElicitationSuggestion s = new ElicitationSuggestion();
        s.id = UUID.randomUUID();
        s.roundId = roundId;
        s.overallSummary = overallSummary;
        s.aiRawResponse = aiRawResponse;
        s.status = SuggestionStatus.PENDING;
        s.createdAt = Instant.now();
        s.updatedAt = s.createdAt;
        return s;
    }

    public static ElicitationSuggestion reconstitute(UUID id, UUID roundId, String overallSummary,
                                                      SuggestionStatus status, String aiRawResponse,
                                                      String createdBy, String updatedBy,
                                                      Instant createdAt, Instant updatedAt) {
        ElicitationSuggestion s = new ElicitationSuggestion();
        s.id = id;
        s.roundId = roundId;
        s.overallSummary = overallSummary;
        s.status = status;
        s.aiRawResponse = aiRawResponse;
        s.createdBy = createdBy;
        s.updatedBy = updatedBy;
        s.createdAt = createdAt;
        s.updatedAt = updatedAt;
        return s;
    }

    public void markPartiallyApproved() {
        this.status = SuggestionStatus.PARTIALLY_APPROVED;
        this.updatedAt = Instant.now();
    }

    public void markCompleted() {
        this.status = SuggestionStatus.COMPLETED;
        this.updatedAt = Instant.now();
    }

    public UUID id()                    { return id; }
    public UUID roundId()               { return roundId; }
    public String overallSummary()      { return overallSummary; }
    public SuggestionStatus status()    { return status; }
    public String aiRawResponse()       { return aiRawResponse; }
    public String createdBy()           { return createdBy; }
    public String updatedBy()           { return updatedBy; }
    public Instant createdAt()          { return createdAt; }
    public Instant updatedAt()          { return updatedAt; }
}

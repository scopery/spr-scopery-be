package com.company.scopery.modules.elicitation.suggestion.domain.model;

import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionItemImpact;
import com.company.scopery.modules.elicitation.suggestion.domain.enums.SuggestionItemStatus;

import java.time.Instant;
import java.util.UUID;

public class ElicitationSuggestionItem {

    private UUID id;
    private UUID suggestionId;
    private int sequence;
    private String action;
    private String targetEntityType;
    private UUID targetEntityId;
    private String targetEntityName;
    private String rationale;
    private String changesJson;
    private String preconditionActionsJson;
    private SuggestionItemImpact estimatedImpact;
    private SuggestionItemStatus status;
    private Instant executedAt;
    private String errorMessage;
    private String rollbackDataJson;
    private UUID requirementId;
    private String chainingContextJson;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    private ElicitationSuggestionItem() {}

    public static ElicitationSuggestionItem create(UUID suggestionId, int sequence, String action,
                                                    String targetEntityType, UUID targetEntityId,
                                                    String targetEntityName, String rationale,
                                                    String changesJson, String preconditionActionsJson,
                                                    SuggestionItemImpact estimatedImpact,
                                                    UUID requirementId, String chainingContextJson) {
        ElicitationSuggestionItem item = new ElicitationSuggestionItem();
        item.id = UUID.randomUUID();
        item.suggestionId = suggestionId;
        item.sequence = sequence;
        item.action = action;
        item.targetEntityType = targetEntityType;
        item.targetEntityId = targetEntityId;
        item.targetEntityName = targetEntityName;
        item.rationale = rationale;
        item.changesJson = changesJson;
        item.preconditionActionsJson = preconditionActionsJson;
        item.estimatedImpact = estimatedImpact;
        item.requirementId = requirementId;
        item.chainingContextJson = chainingContextJson;
        item.status = SuggestionItemStatus.PENDING;
        item.createdAt = Instant.now();
        item.updatedAt = item.createdAt;
        return item;
    }

    public static ElicitationSuggestionItem reconstitute(UUID id, UUID suggestionId, int sequence, String action,
                                                          String targetEntityType, UUID targetEntityId,
                                                          String targetEntityName, String rationale,
                                                          String changesJson, String preconditionActionsJson,
                                                          SuggestionItemImpact estimatedImpact,
                                                          SuggestionItemStatus status, Instant executedAt,
                                                          String errorMessage, String rollbackDataJson,
                                                          UUID requirementId, String chainingContextJson,
                                                          String createdBy, String updatedBy,
                                                          Instant createdAt, Instant updatedAt) {
        ElicitationSuggestionItem item = new ElicitationSuggestionItem();
        item.id = id;
        item.suggestionId = suggestionId;
        item.sequence = sequence;
        item.action = action;
        item.targetEntityType = targetEntityType;
        item.targetEntityId = targetEntityId;
        item.targetEntityName = targetEntityName;
        item.rationale = rationale;
        item.changesJson = changesJson;
        item.preconditionActionsJson = preconditionActionsJson;
        item.estimatedImpact = estimatedImpact;
        item.status = status;
        item.executedAt = executedAt;
        item.errorMessage = errorMessage;
        item.rollbackDataJson = rollbackDataJson;
        item.requirementId = requirementId;
        item.chainingContextJson = chainingContextJson;
        item.createdBy = createdBy;
        item.updatedBy = updatedBy;
        item.createdAt = createdAt;
        item.updatedAt = updatedAt;
        return item;
    }

    public void markApproved() {
        if (status != SuggestionItemStatus.PENDING) throw new IllegalStateException("Item is not pending");
        this.status = SuggestionItemStatus.APPROVED;
        this.updatedAt = Instant.now();
    }

    public void markExecuting() {
        this.status = SuggestionItemStatus.EXECUTING;
        this.updatedAt = Instant.now();
    }

    public void markSucceeded(String rollbackDataJson) {
        this.status = SuggestionItemStatus.SUCCEEDED;
        this.rollbackDataJson = rollbackDataJson;
        this.executedAt = Instant.now();
        this.updatedAt = this.executedAt;
    }

    public void markFailed(String errorMessage) {
        this.status = SuggestionItemStatus.FAILED;
        this.errorMessage = errorMessage;
        this.executedAt = Instant.now();
        this.updatedAt = this.executedAt;
    }

    public void markRejected() {
        if (status != SuggestionItemStatus.PENDING) throw new IllegalStateException("Item is not pending");
        this.status = SuggestionItemStatus.REJECTED;
        this.updatedAt = Instant.now();
    }

    public void editChanges(String changesJson) {
        if (status != SuggestionItemStatus.PENDING && status != SuggestionItemStatus.FAILED) {
            throw new IllegalStateException("Can only edit PENDING or FAILED items");
        }
        this.changesJson = changesJson;
        if (status == SuggestionItemStatus.FAILED) {
            this.status = SuggestionItemStatus.PENDING;
            this.errorMessage = null;
        }
        this.updatedAt = Instant.now();
    }

    public UUID id()                             { return id; }
    public UUID suggestionId()                   { return suggestionId; }
    public int sequence()                        { return sequence; }
    public String action()                       { return action; }
    public String targetEntityType()             { return targetEntityType; }
    public UUID targetEntityId()                 { return targetEntityId; }
    public String targetEntityName()             { return targetEntityName; }
    public String rationale()                    { return rationale; }
    public String changesJson()                  { return changesJson; }
    public String preconditionActionsJson()      { return preconditionActionsJson; }
    public SuggestionItemImpact estimatedImpact() { return estimatedImpact; }
    public SuggestionItemStatus status()         { return status; }
    public Instant executedAt()                  { return executedAt; }
    public String errorMessage()                 { return errorMessage; }
    public String rollbackDataJson()             { return rollbackDataJson; }
    public UUID requirementId()                  { return requirementId; }
    public String chainingContextJson()          { return chainingContextJson; }
    public String createdBy()                    { return createdBy; }
    public String updatedBy()                    { return updatedBy; }
    public Instant createdAt()                   { return createdAt; }
    public Instant updatedAt()                   { return updatedAt; }
}

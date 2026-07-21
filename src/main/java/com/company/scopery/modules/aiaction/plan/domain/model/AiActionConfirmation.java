package com.company.scopery.modules.aiaction.plan.domain.model;

import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionConfirmationDecision;
import com.company.scopery.modules.aiaction.plan.domain.enums.AiActionConfirmationStatus;

import java.time.Instant;
import java.util.UUID;

public class AiActionConfirmation {

    private final UUID id;
    private final UUID planId;
    private final int planVersion;
    private final String planHash;
    private final UUID confirmedByUserId;
    private final AiActionConfirmationDecision decision;
    private final String channel;
    private final String comment;
    private final String confirmationHash;
    private AiActionConfirmationStatus status;
    private final Instant expiresAt;
    private final Instant createdAt;

    private AiActionConfirmation(UUID id, UUID planId, int planVersion, String planHash,
                                  UUID confirmedByUserId, AiActionConfirmationDecision decision,
                                  String channel, String comment, String confirmationHash,
                                  AiActionConfirmationStatus status, Instant expiresAt, Instant createdAt) {
        this.id = id;
        this.planId = planId;
        this.planVersion = planVersion;
        this.planHash = planHash;
        this.confirmedByUserId = confirmedByUserId;
        this.decision = decision;
        this.channel = channel;
        this.comment = comment;
        this.confirmationHash = confirmationHash;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static AiActionConfirmation create(UUID planId, int planVersion, String planHash,
                                               UUID confirmedByUserId, AiActionConfirmationDecision decision,
                                               String channel, String comment,
                                               String confirmationHash, Instant expiresAt) {
        AiActionConfirmationStatus status = decision == AiActionConfirmationDecision.CONFIRM
                ? AiActionConfirmationStatus.CONFIRMED
                : AiActionConfirmationStatus.REJECTED;
        return new AiActionConfirmation(UUID.randomUUID(), planId, planVersion, planHash,
                confirmedByUserId, decision, channel, comment, confirmationHash,
                status, expiresAt, Instant.now());
    }

    public static AiActionConfirmation reconstitute(UUID id, UUID planId, int planVersion, String planHash,
                                                     UUID confirmedByUserId, AiActionConfirmationDecision decision,
                                                     String channel, String comment, String confirmationHash,
                                                     AiActionConfirmationStatus status, Instant expiresAt,
                                                     Instant createdAt) {
        return new AiActionConfirmation(id, planId, planVersion, planHash, confirmedByUserId,
                decision, channel, comment, confirmationHash, status, expiresAt, createdAt);
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isConfirmed() {
        return decision == AiActionConfirmationDecision.CONFIRM
                && status == AiActionConfirmationStatus.CONFIRMED
                && !isExpired();
    }

    public UUID id()                               { return id; }
    public UUID planId()                           { return planId; }
    public int planVersion()                       { return planVersion; }
    public String planHash()                       { return planHash; }
    public UUID confirmedByUserId()                { return confirmedByUserId; }
    public AiActionConfirmationDecision decision() { return decision; }
    public String channel()                        { return channel; }
    public String comment()                        { return comment; }
    public String confirmationHash()               { return confirmationHash; }
    public AiActionConfirmationStatus status()     { return status; }
    public Instant expiresAt()                     { return expiresAt; }
    public Instant createdAt()                     { return createdAt; }
}

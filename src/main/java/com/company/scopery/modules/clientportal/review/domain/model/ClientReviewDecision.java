package com.company.scopery.modules.clientportal.review.domain.model;
import com.company.scopery.modules.clientportal.review.domain.enums.ClientReviewDecisionOutcome;
import java.time.Instant; import java.util.UUID;
public record ClientReviewDecision(UUID id, UUID reviewRequestId, UUID projectId, ClientReviewDecisionOutcome outcome,
                                   String comment, UUID decidedByPortalAccountId, Instant decidedAt,
                                   int version, Instant createdAt) {
    public static ClientReviewDecision create(UUID reviewRequestId, UUID projectId, ClientReviewDecisionOutcome outcome,
                                              String comment, UUID decidedByPortalAccountId) {
        Instant now = Instant.now();
        return new ClientReviewDecision(UUID.randomUUID(), reviewRequestId, projectId, outcome, comment, decidedByPortalAccountId, now, 0, now);
    }
}

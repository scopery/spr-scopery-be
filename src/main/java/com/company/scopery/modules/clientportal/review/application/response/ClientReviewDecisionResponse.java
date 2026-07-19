package com.company.scopery.modules.clientportal.review.application.response;
import com.company.scopery.modules.clientportal.review.domain.model.ClientReviewDecision;
import java.time.Instant; import java.util.UUID;
public record ClientReviewDecisionResponse(UUID id, UUID reviewRequestId, String outcome, String comment,
                                           UUID decidedByPortalAccountId, Instant decidedAt, Instant createdAt) {
    public static ClientReviewDecisionResponse from(ClientReviewDecision d) {
        return new ClientReviewDecisionResponse(d.id(), d.reviewRequestId(), d.outcome().name(), d.comment(),
                d.decidedByPortalAccountId(), d.decidedAt(), d.createdAt());
    }
}

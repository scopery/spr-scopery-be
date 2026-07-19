package com.company.scopery.modules.scope.review.application.response;
import com.company.scopery.modules.scope.review.domain.model.DeliverableReview;
import java.time.Instant; import java.util.UUID;
public record DeliverableReviewResponse(UUID id, UUID deliverableId, UUID projectId, String status, String decision,
        UUID reviewerUserId, String reason, Instant decidedAt, Instant createdAt) {
    public static DeliverableReviewResponse from(DeliverableReview r) {
        return new DeliverableReviewResponse(r.id(), r.deliverableId(), r.projectId(), r.status().name(),
                r.decision(), r.reviewerUserId(), r.reason(), r.decidedAt(), r.createdAt());
    }
}

package com.company.scopery.modules.scope.review.domain.model;
import com.company.scopery.modules.scope.review.domain.enums.ReviewStatus;
import java.time.Instant; import java.util.UUID;
public record DeliverableReview(UUID id, UUID deliverableId, UUID projectId, ReviewStatus status, String decision,
        UUID reviewerUserId, String reason, Instant decidedAt, int version, Instant createdAt) {
    public static DeliverableReview submit(UUID deliverableId, UUID projectId) {
        return new DeliverableReview(UUID.randomUUID(), deliverableId, projectId, ReviewStatus.OPEN,
                null, null, null, null, 0, Instant.now());
    }
    public DeliverableReview approve(UUID reviewerId, String decision) {
        if (status != ReviewStatus.OPEN) throw new IllegalStateException("Only open reviews can be approved");
        return decided(ReviewStatus.APPROVED, reviewerId, decision);
    }
    public DeliverableReview reject(UUID reviewerId, String reason) {
        if (status != ReviewStatus.OPEN) throw new IllegalStateException("Only open reviews can be rejected");
        return decided(ReviewStatus.REJECTED, reviewerId, reason);
    }
    public DeliverableReview requestRework(UUID reviewerId, String reason) {
        if (status != ReviewStatus.OPEN) throw new IllegalStateException("Only open reviews can request rework");
        return decided(ReviewStatus.REWORK_REQUESTED, reviewerId, reason);
    }
    private DeliverableReview decided(ReviewStatus newStatus, UUID reviewerId, String reasonOrDecision) {
        return new DeliverableReview(id, deliverableId, projectId, newStatus,
                newStatus == ReviewStatus.APPROVED ? reasonOrDecision : null,
                reviewerId, reasonOrDecision, Instant.now(), version, createdAt);
    }
}

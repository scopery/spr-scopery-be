package com.company.scopery.modules.scope.review.infrastructure.mapper;
import com.company.scopery.modules.scope.review.domain.enums.ReviewStatus;
import com.company.scopery.modules.scope.review.domain.model.DeliverableReview;
import com.company.scopery.modules.scope.review.infrastructure.persistence.DeliverableReviewJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class DeliverableReviewPersistenceMapper {
    public DeliverableReview toDomain(DeliverableReviewJpaEntity e) {
        return new DeliverableReview(e.getId(), e.getDeliverableId(), e.getProjectId(),
                ReviewStatus.valueOf(e.getStatus()), e.getDecision(), e.getReviewerUserId(), e.getReason(),
                e.getDecidedAt(), e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
    public DeliverableReviewJpaEntity toJpaEntity(DeliverableReview d) {
        DeliverableReviewJpaEntity e = new DeliverableReviewJpaEntity();
        e.setId(d.id()); e.setDeliverableId(d.deliverableId()); e.setProjectId(d.projectId());
        e.setStatus(d.status().name()); e.setDecision(d.decision()); e.setReviewerUserId(d.reviewerUserId());
        e.setReason(d.reason()); e.setDecidedAt(d.decidedAt()); e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}

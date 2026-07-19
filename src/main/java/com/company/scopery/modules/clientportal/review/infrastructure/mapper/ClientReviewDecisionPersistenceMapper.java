package com.company.scopery.modules.clientportal.review.infrastructure.mapper;
import com.company.scopery.modules.clientportal.review.domain.enums.ClientReviewDecisionOutcome;
import com.company.scopery.modules.clientportal.review.domain.model.ClientReviewDecision;
import com.company.scopery.modules.clientportal.review.infrastructure.persistence.ClientReviewDecisionJpaEntity;
import org.springframework.stereotype.Component;
@Component
public class ClientReviewDecisionPersistenceMapper {
    public ClientReviewDecision toDomain(ClientReviewDecisionJpaEntity e) {
        return new ClientReviewDecision(e.getId(), e.getReviewRequestId(), e.getProjectId(),
                ClientReviewDecisionOutcome.valueOf(e.getDecision()), e.getComment(),
                e.getDecidedByPortalAccountId(), e.getDecidedAt(),
                e.getVersion() == null ? 0 : e.getVersion(), e.getCreatedAt());
    }
    public ClientReviewDecisionJpaEntity toJpaEntity(ClientReviewDecision d) {
        ClientReviewDecisionJpaEntity e = new ClientReviewDecisionJpaEntity();
        e.setId(d.id()); e.setReviewRequestId(d.reviewRequestId()); e.setProjectId(d.projectId());
        e.setDecision(d.outcome().name()); e.setComment(d.comment());
        e.setDecidedByPortalAccountId(d.decidedByPortalAccountId()); e.setDecidedAt(d.decidedAt());
        e.setVersion(d.version());
        if (d.createdAt() != null) e.setCreatedAt(d.createdAt());
        return e;
    }
}

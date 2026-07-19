package com.company.scopery.modules.clientportal.review.domain.model;
import java.util.List;
import java.util.UUID;
public interface ClientReviewDecisionRepository {
    ClientReviewDecision save(ClientReviewDecision entity);
    List<ClientReviewDecision> findByReviewRequestId(UUID reviewRequestId);
}

package com.company.scopery.modules.scope.review.domain.model;
import com.company.scopery.modules.scope.review.domain.enums.ReviewStatus;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface DeliverableReviewRepository {
    DeliverableReview save(DeliverableReview review);
    Optional<DeliverableReview> findByIdAndProjectId(UUID id, UUID projectId);
    Optional<DeliverableReview> findOpenByDeliverableId(UUID deliverableId);
    List<DeliverableReview> findByDeliverableId(UUID deliverableId);
}

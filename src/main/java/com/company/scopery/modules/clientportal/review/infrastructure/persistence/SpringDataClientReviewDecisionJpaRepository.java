package com.company.scopery.modules.clientportal.review.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.UUID;
public interface SpringDataClientReviewDecisionJpaRepository extends JpaRepository<ClientReviewDecisionJpaEntity, UUID> {
    List<ClientReviewDecisionJpaEntity> findByReviewRequestId(UUID reviewRequestId);
}

package com.company.scopery.modules.clientportal.review.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface SpringDataClientReviewRequestJpaRepository extends JpaRepository<ClientReviewRequestJpaEntity, UUID> {
    Optional<ClientReviewRequestJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ClientReviewRequestJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}

package com.company.scopery.modules.scope.deliverable.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataDeliverableJpaRepository extends JpaRepository<DeliverableJpaEntity, UUID> {
    Optional<DeliverableJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<DeliverableJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}

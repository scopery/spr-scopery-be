package com.company.scopery.modules.scope.review.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; import java.util.UUID;
public interface SpringDataDeliverableAcceptanceJpaRepository extends JpaRepository<DeliverableAcceptanceJpaEntity, UUID> {
    Optional<DeliverableAcceptanceJpaEntity> findFirstByDeliverableIdOrderByAcceptedAtDesc(UUID deliverableId);
}

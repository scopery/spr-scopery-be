package com.company.scopery.modules.scope.evidence.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataAcceptanceEvidenceJpaRepository extends JpaRepository<AcceptanceEvidenceJpaEntity, UUID> {
    Optional<AcceptanceEvidenceJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<AcceptanceEvidenceJpaEntity> findByDeliverableIdOrderByCreatedAtDesc(UUID deliverableId);
    long countByProjectId(UUID projectId);
}

package com.company.scopery.modules.scope.mapping.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; import java.util.Optional; import java.util.UUID;
public interface SpringDataDeliverableTaskMappingJpaRepository extends JpaRepository<DeliverableTaskMappingJpaEntity, UUID> {
    Optional<DeliverableTaskMappingJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<DeliverableTaskMappingJpaEntity> findByDeliverableIdAndArchivedAtIsNullOrderByCreatedAtDesc(UUID deliverableId);
}

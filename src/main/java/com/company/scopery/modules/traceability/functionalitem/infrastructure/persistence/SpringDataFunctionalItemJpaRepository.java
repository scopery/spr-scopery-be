package com.company.scopery.modules.traceability.functionalitem.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataFunctionalItemJpaRepository extends JpaRepository<FunctionalItemJpaEntity, UUID> {
    List<FunctionalItemJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    List<FunctionalItemJpaEntity> findByProjectIdAndModuleIdOrderByCreatedAtDesc(UUID projectId, UUID moduleId);
    List<FunctionalItemJpaEntity> findByModuleIdIn(Collection<UUID> moduleIds);
    Optional<FunctionalItemJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
    void deleteByIdAndProjectId(UUID id, UUID projectId);
}

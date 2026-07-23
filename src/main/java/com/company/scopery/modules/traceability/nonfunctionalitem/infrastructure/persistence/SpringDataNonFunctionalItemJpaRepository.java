package com.company.scopery.modules.traceability.nonfunctionalitem.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataNonFunctionalItemJpaRepository
        extends JpaRepository<NonFunctionalItemJpaEntity, UUID> {

    List<NonFunctionalItemJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

    List<NonFunctionalItemJpaEntity> findByProjectIdAndIdIn(UUID projectId, Collection<UUID> ids);

    Optional<NonFunctionalItemJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);

    boolean existsByProjectIdAndCode(UUID projectId, String code);

    void deleteByIdAndProjectId(UUID id, UUID projectId);
}

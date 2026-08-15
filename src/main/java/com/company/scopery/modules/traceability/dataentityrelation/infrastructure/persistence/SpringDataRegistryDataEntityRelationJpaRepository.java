package com.company.scopery.modules.traceability.dataentityrelation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRegistryDataEntityRelationJpaRepository
        extends JpaRepository<RegistryDataEntityRelationJpaEntity, UUID> {

    Optional<RegistryDataEntityRelationJpaEntity> findByIdAndWorkspaceId(UUID id, UUID workspaceId);

    @Query("SELECT e FROM RegistryDataEntityRelationJpaEntity e " +
           "WHERE e.sourceEntityId = :entityId OR e.targetEntityId = :entityId " +
           "ORDER BY e.createdAt ASC")
    List<RegistryDataEntityRelationJpaEntity> findByEntityId(@Param("entityId") UUID entityId);

    boolean existsBySourceEntityIdAndTargetEntityIdAndRelationType(UUID sourceEntityId, UUID targetEntityId, String relationType);
}

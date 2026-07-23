package com.company.scopery.modules.traceability.funcitemanchor.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataFunctionalItemAnchorJpaRepository
        extends JpaRepository<FunctionalItemAnchorJpaEntity, UUID> {

    List<FunctionalItemAnchorJpaEntity> findByFunctionalItemIdOrderByCreatedAtAsc(UUID functionalItemId);

    Optional<FunctionalItemAnchorJpaEntity> findByIdAndFunctionalItemId(UUID id, UUID functionalItemId);

    boolean existsByFunctionalItemIdAndNodeTypeAndNodeId(UUID functionalItemId, String nodeType, UUID nodeId);

    void deleteByIdAndFunctionalItemId(UUID id, UUID functionalItemId);

    @Query("SELECT a FROM FunctionalItemAnchorJpaEntity a WHERE a.functionalItemId IN " +
           "(SELECT fi.id FROM FunctionalItemJpaEntity fi WHERE fi.projectId = :projectId) " +
           "ORDER BY a.createdAt ASC")
    List<FunctionalItemAnchorJpaEntity> findByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT a FROM FunctionalItemAnchorJpaEntity a WHERE a.nodeType = :nodeType AND a.nodeId = :nodeId " +
           "AND a.functionalItemId IN (SELECT fi.id FROM FunctionalItemJpaEntity fi WHERE fi.projectId = :projectId) " +
           "ORDER BY a.createdAt ASC")
    List<FunctionalItemAnchorJpaEntity> findByNodeTypeAndNodeIdAndProjectId(
            @Param("nodeType") String nodeType, @Param("nodeId") UUID nodeId, @Param("projectId") UUID projectId);
}

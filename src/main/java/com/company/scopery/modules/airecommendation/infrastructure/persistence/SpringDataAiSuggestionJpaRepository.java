package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiSuggestionJpaRepository
        extends JpaRepository<AiSuggestionJpaEntity, UUID>, JpaSpecificationExecutor<AiSuggestionJpaEntity> {

    @Query("""
            SELECT e FROM AiSuggestionJpaEntity e
            WHERE e.workspaceId = :workspaceId
              AND e.dedupKey = :dedupKey
              AND e.status IN ('GENERATED', 'VIEWED', 'EDITED', 'ACCEPTED')
            """)
    Optional<AiSuggestionJpaEntity> findActiveByWorkspaceAndDedupKey(
            @Param("workspaceId") UUID workspaceId,
            @Param("dedupKey") String dedupKey);

    @Query("""
            SELECT e FROM AiSuggestionJpaEntity e
            WHERE e.workspaceId = :workspaceId
              AND e.targetEntityType = :entityType
              AND e.targetEntityId = :entityId
              AND (:projectId IS NULL OR e.projectId = :projectId)
            """)
    Page<AiSuggestionJpaEntity> findByWorkspaceAndTargetEntity(
            @Param("workspaceId") UUID workspaceId,
            @Param("entityType") String entityType,
            @Param("entityId") UUID entityId,
            @Param("projectId") UUID projectId,
            Pageable pageable);

    @Query("""
            SELECT e FROM AiSuggestionJpaEntity e
            WHERE e.status IN ('GENERATED', 'VIEWED', 'EDITED', 'ACCEPTED')
              AND e.expiresAt < :before
            """)
    List<AiSuggestionJpaEntity> findExpiredAndActive(
            @Param("before") Instant before,
            Pageable pageable);
}

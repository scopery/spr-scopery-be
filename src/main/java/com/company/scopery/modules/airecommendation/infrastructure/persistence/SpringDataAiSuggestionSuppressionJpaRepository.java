package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiSuggestionSuppressionJpaRepository
        extends JpaRepository<AiSuggestionSuppressionJpaEntity, UUID> {

    @Query("""
            SELECT e FROM AiSuggestionSuppressionJpaEntity e
            WHERE e.workspaceId = :workspaceId
              AND (:projectId IS NULL OR e.projectId = :projectId)
              AND e.actorId = :actorId
              AND e.suppressionKey = :suppressionKey
              AND e.active = true
            """)
    Optional<AiSuggestionSuppressionJpaEntity> findActiveByKey(
            @Param("workspaceId") UUID workspaceId,
            @Param("projectId") UUID projectId,
            @Param("actorId") UUID actorId,
            @Param("suppressionKey") String suppressionKey);

    @Query("""
            SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
            FROM AiSuggestionSuppressionJpaEntity e
            WHERE e.workspaceId = :workspaceId
              AND (:projectId IS NULL OR e.projectId = :projectId)
              AND e.actorId = :actorId
              AND e.suggestionType = :suggestionType
              AND (:targetEntityType IS NULL OR e.targetEntityType = :targetEntityType)
              AND (:targetEntityId IS NULL OR e.targetEntityId = :targetEntityId)
              AND e.active = true
            """)
    boolean hasActiveSuppressionFor(
            @Param("workspaceId") UUID workspaceId,
            @Param("projectId") UUID projectId,
            @Param("actorId") UUID actorId,
            @Param("suggestionType") String suggestionType,
            @Param("targetEntityType") String targetEntityType,
            @Param("targetEntityId") UUID targetEntityId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE AiSuggestionSuppressionJpaEntity e
            SET e.active = false, e.updatedAt = CURRENT_TIMESTAMP
            WHERE e.active = true AND e.expiresAt < :before
            """)
    int deactivateExpiredBefore(@Param("before") Instant before);
}

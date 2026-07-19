package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataAiSuggestionEvidenceJpaRepository
        extends JpaRepository<AiSuggestionEvidenceJpaEntity, UUID> {

    List<AiSuggestionEvidenceJpaEntity> findBySuggestionIdOrderByOrdinal(UUID suggestionId);

    @Query("""
            SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
            FROM AiSuggestionEvidenceJpaEntity e
            WHERE e.suggestionId = :suggestionId
              AND e.sourceType = :sourceType
              AND (:sourceRefId IS NULL OR e.sourceRefId = :sourceRefId)
              AND (:knowledgeChunkId IS NULL OR e.knowledgeChunkId = :knowledgeChunkId)
            """)
    boolean existsBySuggestionAndSourceKey(
            @Param("suggestionId") UUID suggestionId,
            @Param("sourceType") String sourceType,
            @Param("sourceRefId") UUID sourceRefId,
            @Param("knowledgeChunkId") UUID knowledgeChunkId);
}

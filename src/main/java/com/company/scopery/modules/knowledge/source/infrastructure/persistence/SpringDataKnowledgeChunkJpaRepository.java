package com.company.scopery.modules.knowledge.source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataKnowledgeChunkJpaRepository extends JpaRepository<KnowledgeChunkJpaEntity, UUID> {
    List<KnowledgeChunkJpaEntity> findBySourceIdAndIsCurrentTrueOrderByChunkOrdinalAsc(UUID sourceId);
    List<KnowledgeChunkJpaEntity> findByProjectionIdOrderByChunkOrdinalAsc(UUID projectionId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE KnowledgeChunkJpaEntity c SET c.isCurrent = false WHERE c.sourceId = :sourceId AND c.isCurrent = true")
    int markSupersededBySourceId(@Param("sourceId") UUID sourceId);
}

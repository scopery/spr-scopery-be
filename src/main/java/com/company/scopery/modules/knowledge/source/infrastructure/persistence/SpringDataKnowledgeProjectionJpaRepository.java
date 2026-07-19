package com.company.scopery.modules.knowledge.source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataKnowledgeProjectionJpaRepository extends JpaRepository<KnowledgeProjectionJpaEntity, UUID> {
    List<KnowledgeProjectionJpaEntity> findBySourceIdOrderByProjectionVersionDesc(UUID sourceId);

    @Query("SELECT p FROM KnowledgeProjectionJpaEntity p WHERE p.sourceId = :sourceId ORDER BY p.projectionVersion DESC LIMIT 1")
    Optional<KnowledgeProjectionJpaEntity> findLatestBySourceId(@Param("sourceId") UUID sourceId);

    @Query("SELECT COALESCE(MAX(p.projectionVersion), 0) FROM KnowledgeProjectionJpaEntity p WHERE p.sourceId = :sourceId")
    int maxProjectionVersion(@Param("sourceId") UUID sourceId);
}

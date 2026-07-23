package com.company.scopery.modules.knowledge.source.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataKnowledgeSourceJpaRepository extends JpaRepository<KnowledgeSourceJpaEntity, UUID> {
    Optional<KnowledgeSourceJpaEntity> findByWorkspaceIdAndSourceTypeAndSourceRefIdAndSourceVersionRefId(
            UUID workspaceId, String sourceType, UUID sourceRefId, UUID sourceVersionRefId);
    java.util.List<KnowledgeSourceJpaEntity> findByWorkspaceId(UUID workspaceId);
    java.util.List<KnowledgeSourceJpaEntity> findByProjectId(UUID projectId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE KnowledgeSourceJpaEntity e SET e.sourceStatus = :status, e.lastIndexedAt = :indexedAt, e.updatedAt = :indexedAt, e.version = e.version + 1 WHERE e.id = :id")
    int markIndexed(@Param("id") UUID id, @Param("status") String status, @Param("indexedAt") Instant indexedAt);
}

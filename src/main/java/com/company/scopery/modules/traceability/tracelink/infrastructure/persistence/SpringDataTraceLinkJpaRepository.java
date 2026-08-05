package com.company.scopery.modules.traceability.tracelink.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface SpringDataTraceLinkJpaRepository extends JpaRepository<TraceLinkJpaEntity, UUID> {
    Optional<TraceLinkJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);

    @Query("SELECT t FROM TraceLinkJpaEntity t WHERE t.projectId = :projectId AND t.status <> 'ARCHIVED' ORDER BY t.createdAt DESC")
    List<TraceLinkJpaEntity> findActiveByProjectIdOrderByCreatedAtDesc(@Param("projectId") UUID projectId);

    @Query("SELECT t FROM TraceLinkJpaEntity t WHERE t.projectId = :projectId " +
           "AND t.sourceType = :sourceType AND t.sourceId = :sourceId " +
           "AND t.targetType = :targetType AND t.targetId = :targetId " +
           "AND t.linkType = :linkType AND t.status = 'ACTIVE'")
    List<TraceLinkJpaEntity> findActiveBySourceAndTarget(@Param("projectId") UUID projectId,
                                                         @Param("sourceType") String sourceType,
                                                         @Param("sourceId") UUID sourceId,
                                                         @Param("targetType") String targetType,
                                                         @Param("targetId") UUID targetId,
                                                         @Param("linkType") String linkType);

    @Query("SELECT COUNT(t) > 0 FROM TraceLinkJpaEntity t " +
           "WHERE t.projectId = :projectId AND t.sourceType = :sourceType AND t.sourceId = :sourceId " +
           "AND t.targetType = :targetType AND t.targetId = :targetId AND t.linkType = :linkType AND t.status = 'ACTIVE'")
    boolean existsActiveLink(@Param("projectId") UUID projectId,
                             @Param("sourceType") String sourceType,
                             @Param("sourceId") UUID sourceId,
                             @Param("targetType") String targetType,
                             @Param("targetId") UUID targetId,
                             @Param("linkType") String linkType);
}

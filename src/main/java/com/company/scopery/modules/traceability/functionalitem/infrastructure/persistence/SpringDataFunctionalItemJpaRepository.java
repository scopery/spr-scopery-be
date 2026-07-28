package com.company.scopery.modules.traceability.functionalitem.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataFunctionalItemJpaRepository extends JpaRepository<FunctionalItemJpaEntity, UUID> {
    List<FunctionalItemJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

    @org.springframework.data.jpa.repository.Query(
        "SELECT f FROM FunctionalItemJpaEntity f WHERE f.projectId = :projectId " +
        "AND (LOWER(f.code) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(f.title) LIKE LOWER(CONCAT('%', :q, '%'))) " +
        "ORDER BY f.code ASC")
    List<FunctionalItemJpaEntity> searchByProjectIdAndQ(
        @org.springframework.data.repository.query.Param("projectId") UUID projectId,
        @org.springframework.data.repository.query.Param("q") String q,
        org.springframework.data.domain.Pageable pageable);
    List<FunctionalItemJpaEntity> findByProjectIdAndModuleIdOrderByCreatedAtDesc(UUID projectId, UUID moduleId);
    List<FunctionalItemJpaEntity> findByModuleIdIn(Collection<UUID> moduleIds);
    Optional<FunctionalItemJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
    void deleteByIdAndProjectId(UUID id, UUID projectId);
    Optional<FunctionalItemJpaEntity> findByProjectIdAndCode(UUID projectId, String code);
    @Query(value = """
            SELECT id, code, title, similarity(title, :searchTitle) AS score
            FROM app_functional_item
            WHERE project_id = :projectId
              AND status != 'ARCHIVED'
              AND similarity(title, :searchTitle) > :threshold
            ORDER BY score DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findSimilarByTitle(@Param("projectId") UUID projectId,
                                      @Param("searchTitle") String searchTitle,
                                      @Param("threshold") double threshold,
                                      @Param("limit") int limit);
}

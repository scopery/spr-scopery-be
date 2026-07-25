package com.company.scopery.modules.traceability.functionalitem.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
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
}

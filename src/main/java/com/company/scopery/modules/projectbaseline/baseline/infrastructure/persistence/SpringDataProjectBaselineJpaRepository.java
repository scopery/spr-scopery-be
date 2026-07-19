package com.company.scopery.modules.projectbaseline.baseline.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectBaselineJpaRepository extends JpaRepository<ProjectBaselineJpaEntity, UUID> {
    List<ProjectBaselineJpaEntity> findByProjectIdOrderByBaselineNumberDesc(UUID projectId);
    Optional<ProjectBaselineJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    Optional<ProjectBaselineJpaEntity> findByProjectIdAndCurrentFlagTrue(UUID projectId);
    List<ProjectBaselineJpaEntity> findByProjectIdAndCurrentFlagTrueOrderByBaselineNumberDesc(UUID projectId);

    @Query("select coalesce(max(e.baselineNumber), 0) from ProjectBaselineJpaEntity e where e.projectId = :projectId")
    int findMaxBaselineNumber(@Param("projectId") UUID projectId);
}

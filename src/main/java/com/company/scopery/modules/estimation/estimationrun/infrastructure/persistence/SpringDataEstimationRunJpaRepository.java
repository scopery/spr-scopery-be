package com.company.scopery.modules.estimation.estimationrun.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataEstimationRunJpaRepository extends JpaRepository<EstimationRunJpaEntity, UUID> {

    List<EstimationRunJpaEntity> findAllByProjectIdOrderByCreatedAtDesc(UUID projectId);

    @Query("SELECT e FROM EstimationRunJpaEntity e WHERE e.projectId = :projectId AND e.status = 'COMPLETED' ORDER BY e.completedAt DESC")
    List<EstimationRunJpaEntity> findCompletedByProjectId(@Param("projectId") UUID projectId);
}

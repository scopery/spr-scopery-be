package com.company.scopery.modules.quality.rollbackplan.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRollbackPlanJpaRepository extends JpaRepository<RollbackPlanJpaEntity, UUID> {
    Optional<RollbackPlanJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<RollbackPlanJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}

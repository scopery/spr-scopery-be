package com.company.scopery.modules.quality.qualityplan.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataQualityPlanJpaRepository extends JpaRepository<QualityPlanJpaEntity, UUID> {
    Optional<QualityPlanJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<QualityPlanJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

}

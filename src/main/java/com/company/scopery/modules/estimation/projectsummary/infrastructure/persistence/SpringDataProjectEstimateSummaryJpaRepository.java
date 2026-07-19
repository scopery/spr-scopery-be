package com.company.scopery.modules.estimation.projectsummary.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; import java.util.UUID;
public interface SpringDataProjectEstimateSummaryJpaRepository extends JpaRepository<ProjectEstimateSummaryJpaEntity, UUID> {
    Optional<ProjectEstimateSummaryJpaEntity> findByEstimationRunId(UUID estimationRunId);
}

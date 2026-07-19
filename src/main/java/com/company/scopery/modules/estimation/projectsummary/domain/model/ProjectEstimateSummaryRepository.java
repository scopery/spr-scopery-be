package com.company.scopery.modules.estimation.projectsummary.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface ProjectEstimateSummaryRepository {
    ProjectEstimateSummary save(ProjectEstimateSummary summary);
    Optional<ProjectEstimateSummary> findByEstimationRunId(UUID estimationRunId);
}

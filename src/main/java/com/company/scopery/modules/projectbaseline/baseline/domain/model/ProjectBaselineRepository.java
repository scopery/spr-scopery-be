package com.company.scopery.modules.projectbaseline.baseline.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectBaselineRepository {
    ProjectBaseline save(ProjectBaseline baseline);
    Optional<ProjectBaseline> findById(UUID id);
    Optional<ProjectBaseline> findByIdAndProjectId(UUID id, UUID projectId);
    List<ProjectBaseline> findByProjectId(UUID projectId);
    Optional<ProjectBaseline> findCurrentByProjectId(UUID projectId);
    List<ProjectBaseline> findCurrentFlagged(UUID projectId);
    int nextBaselineNumber(UUID projectId);
}

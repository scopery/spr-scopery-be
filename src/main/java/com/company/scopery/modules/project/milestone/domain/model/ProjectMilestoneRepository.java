package com.company.scopery.modules.project.milestone.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMilestoneRepository {
    ProjectMilestone save(ProjectMilestone milestone);
    Optional<ProjectMilestone> findById(UUID id);
    List<ProjectMilestone> findAllByProjectId(UUID projectId);
    boolean existsByProjectIdAndCode(UUID projectId, String code);
}

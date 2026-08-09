package com.company.scopery.modules.project.taskrolecontribution.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRoleContributionRepository {
    TaskRoleContribution save(TaskRoleContribution contribution);
    Optional<TaskRoleContribution> findById(UUID id);
    List<TaskRoleContribution> findAllByTaskId(UUID taskId);
    List<TaskRoleContribution> findAllByProjectId(UUID projectId);
    void deleteById(UUID id);
}

package com.company.scopery.modules.project.taskrolecontribution.application.service;

import com.company.scopery.modules.project.taskrolecontribution.application.response.TaskRoleContributionResponse;
import com.company.scopery.modules.project.taskrolecontribution.domain.model.TaskRoleContributionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskRoleContributionQueryService {

    private final TaskRoleContributionRepository contributions;

    public TaskRoleContributionQueryService(TaskRoleContributionRepository contributions) {
        this.contributions = contributions;
    }

    @Transactional(readOnly = true)
    public List<TaskRoleContributionResponse> listByTask(UUID taskId) {
        return contributions.findAllByTaskId(taskId).stream()
                .map(TaskRoleContributionResponse::from)
                .toList();
    }
}

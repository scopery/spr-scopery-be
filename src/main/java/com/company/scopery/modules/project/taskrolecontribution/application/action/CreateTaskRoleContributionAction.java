package com.company.scopery.modules.project.taskrolecontribution.application.action;

import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.task.domain.model.Task;
import com.company.scopery.modules.project.task.domain.model.TaskRepository;
import com.company.scopery.modules.project.taskrolecontribution.application.command.CreateTaskRoleContributionCommand;
import com.company.scopery.modules.project.taskrolecontribution.application.response.TaskRoleContributionResponse;
import com.company.scopery.modules.project.taskrolecontribution.domain.model.TaskRoleContribution;
import com.company.scopery.modules.project.taskrolecontribution.domain.model.TaskRoleContributionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateTaskRoleContributionAction {

    private final TaskRoleContributionRepository contributions;
    private final TaskRepository tasks;
    private final ProjectMutationGuard mutationGuard;

    public CreateTaskRoleContributionAction(TaskRoleContributionRepository contributions,
                                            TaskRepository tasks,
                                            ProjectMutationGuard mutationGuard) {
        this.contributions = contributions;
        this.tasks = tasks;
        this.mutationGuard = mutationGuard;
    }

    @Transactional
    public TaskRoleContributionResponse execute(CreateTaskRoleContributionCommand cmd) {
        Task task = tasks.findById(cmd.taskId())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.taskId()));

        if (!task.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskProjectMismatch(task.id(), cmd.projectId());
        }

        mutationGuard.requireMutableProject(task.projectId());

        TaskRoleContribution contribution = TaskRoleContribution.create(
                cmd.projectId(), cmd.taskId(), cmd.userId(),
                cmd.costRoleCode(), cmd.costRoleName(),
                cmd.plannedHours(), cmd.rateSnapshotPerHour(), cmd.currencyCode(),
                cmd.periodStart(), cmd.periodEnd(), cmd.notes());

        return TaskRoleContributionResponse.from(contributions.save(contribution));
    }
}

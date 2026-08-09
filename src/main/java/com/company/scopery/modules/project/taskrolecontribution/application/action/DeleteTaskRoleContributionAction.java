package com.company.scopery.modules.project.taskrolecontribution.application.action;

import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.taskrolecontribution.application.command.DeleteTaskRoleContributionCommand;
import com.company.scopery.modules.project.taskrolecontribution.domain.model.TaskRoleContribution;
import com.company.scopery.modules.project.taskrolecontribution.domain.model.TaskRoleContributionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteTaskRoleContributionAction {

    private final TaskRoleContributionRepository contributions;
    private final ProjectMutationGuard mutationGuard;

    public DeleteTaskRoleContributionAction(TaskRoleContributionRepository contributions,
                                            ProjectMutationGuard mutationGuard) {
        this.contributions = contributions;
        this.mutationGuard = mutationGuard;
    }

    @Transactional
    public void execute(DeleteTaskRoleContributionCommand cmd) {
        TaskRoleContribution contribution = contributions.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.taskNotFound(cmd.id()));

        if (!contribution.taskId().equals(cmd.taskId()) || !contribution.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.taskNotFound(cmd.id());
        }

        mutationGuard.requireMutableProject(contribution.projectId());
        contributions.deleteById(cmd.id());
    }
}

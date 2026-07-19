package com.company.scopery.modules.project.milestone.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.milestone.application.command.AchieveProjectMilestoneCommand;
import com.company.scopery.modules.project.milestone.application.response.MilestoneResponse;
import com.company.scopery.modules.project.milestone.domain.enums.MilestoneStatus;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestone;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestoneRepository;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AchieveProjectMilestoneAction {

    private final ProjectMilestoneRepository milestones;
    private final ProjectWorkspaceAuthorizationService authorization;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher publisher;
    private final ProjectActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUser;

    public AchieveProjectMilestoneAction(ProjectMilestoneRepository milestones,
                                         ProjectWorkspaceAuthorizationService authorization,
                                         ProjectMutationGuard mutationGuard,
                                         ProjectPlatformPublisher publisher,
                                         ProjectActivityLogger activityLogger,
                                         CurrentUserAuthorizationService currentUser) {
        this.milestones = milestones;
        this.authorization = authorization;
        this.mutationGuard = mutationGuard;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
        this.currentUser = currentUser;
    }

    @Transactional
    public MilestoneResponse execute(AchieveProjectMilestoneCommand cmd) {
        authorization.requireGanttMilestoneUpdate(cmd.projectId());
        Project project = mutationGuard.requireMutableProject(cmd.projectId());

        ProjectMilestone existing = milestones.findById(cmd.milestoneId())
                .orElseThrow(() -> ProjectExceptions.projectMilestoneNotFound(cmd.milestoneId()));
        if (!existing.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.projectMilestonePathMismatch(cmd.milestoneId(), cmd.projectId());
        }
        if (existing.status() == MilestoneStatus.ARCHIVED) {
            throw ProjectExceptions.projectMilestoneArchived(existing.id());
        }

        var actorId = currentUser.resolveCurrentUser().id();
        ProjectMilestone saved = milestones.save(existing.achieve(actorId));
        publisher.enqueueMilestone(saved, "PROJECT_MILESTONE_ACHIEVED");
        publisher.auditMilestoneAchieved(actorId, project, saved);
        activityLogger.logSuccess(ProjectEntityTypes.PROJECT_MILESTONE, saved.id(),
                ProjectActivityActions.PROJECT_MILESTONE_ACHIEVED, "Milestone achieved: " + saved.name());
        return MilestoneResponse.from(saved);
    }
}

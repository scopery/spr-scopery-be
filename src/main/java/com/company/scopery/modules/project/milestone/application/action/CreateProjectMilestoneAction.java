package com.company.scopery.modules.project.milestone.application.action;

import com.company.scopery.modules.project.milestone.application.command.CreateProjectMilestoneCommand;
import com.company.scopery.modules.project.milestone.application.response.MilestoneResponse;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestone;
import com.company.scopery.modules.project.milestone.domain.model.ProjectMilestoneRepository;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateProjectMilestoneAction {

    private final ProjectMilestoneRepository milestones;
    private final ProjectPhaseRepository phases;
    private final WbsNodeRepository wbsNodes;
    private final ProjectWorkspaceAuthorizationService authorization;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher publisher;
    private final ProjectActivityLogger activityLogger;

    public CreateProjectMilestoneAction(ProjectMilestoneRepository milestones,
                                        ProjectPhaseRepository phases,
                                        WbsNodeRepository wbsNodes,
                                        ProjectWorkspaceAuthorizationService authorization,
                                        ProjectMutationGuard mutationGuard,
                                        ProjectPlatformPublisher publisher,
                                        ProjectActivityLogger activityLogger) {
        this.milestones = milestones;
        this.phases = phases;
        this.wbsNodes = wbsNodes;
        this.authorization = authorization;
        this.mutationGuard = mutationGuard;
        this.publisher = publisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public MilestoneResponse execute(CreateProjectMilestoneCommand cmd) {
        authorization.requireGanttMilestoneCreate(cmd.projectId());
        mutationGuard.requireMutableProject(cmd.projectId());

        if (cmd.milestoneDate() == null) {
            throw ProjectExceptions.projectMilestoneInvalidDate();
        }
        if (cmd.projectPhaseId() != null) {
            var phase = phases.findById(cmd.projectPhaseId())
                    .orElseThrow(() -> ProjectExceptions.projectPhaseNotFound(cmd.projectPhaseId()));
            if (!phase.projectId().equals(cmd.projectId())) {
                throw ProjectExceptions.projectMilestonePhaseMismatch(cmd.projectPhaseId(), cmd.projectId());
            }
        }
        if (cmd.wbsNodeId() != null) {
            var node = wbsNodes.findById(cmd.wbsNodeId())
                    .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(cmd.wbsNodeId()));
            if (!node.projectId().equals(cmd.projectId())) {
                throw ProjectExceptions.projectMilestoneWbsMismatch(cmd.wbsNodeId(), cmd.projectId());
            }
        }
        if (cmd.code() != null && !cmd.code().isBlank()
                && milestones.existsByProjectIdAndCode(cmd.projectId(), cmd.code())) {
            throw ProjectExceptions.projectMilestoneCodeAlreadyExists(cmd.code(), cmd.projectId());
        }

        ProjectMilestone saved = milestones.save(ProjectMilestone.create(
                cmd.projectId(), cmd.projectPhaseId(), cmd.wbsNodeId(),
                cmd.code(), cmd.name(), cmd.description(), cmd.milestoneDate(), cmd.sortOrder()));

        publisher.enqueueMilestone(saved, "PROJECT_MILESTONE_CREATED");
        activityLogger.logSuccess(ProjectEntityTypes.PROJECT_MILESTONE, saved.id(),
                ProjectActivityActions.PROJECT_MILESTONE_CREATED, "Milestone created: " + saved.name());
        return MilestoneResponse.from(saved);
    }
}

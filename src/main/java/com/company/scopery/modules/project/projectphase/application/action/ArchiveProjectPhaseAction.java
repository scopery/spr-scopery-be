package com.company.scopery.modules.project.projectphase.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.projectphase.application.command.ArchiveProjectPhaseCommand;
import com.company.scopery.modules.project.projectphase.application.response.ProjectPhaseResponse;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveProjectPhaseAction {

    private final ProjectPhaseRepository projectPhaseRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public ArchiveProjectPhaseAction(ProjectPhaseRepository projectPhaseRepository,
                                     ProjectActivityLogger activityLogger,
                                     ProjectWorkspaceAuthorizationService authorizationService) {
        this.projectPhaseRepository = projectPhaseRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public ProjectPhaseResponse execute(ArchiveProjectPhaseCommand cmd) {
        var phase = projectPhaseRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.projectPhaseNotFound(cmd.id()));

        if (!phase.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.projectPhaseProjectMismatch(phase.id(), cmd.projectId());
        }

        authorizationService.requireProjectPermission(phase.projectId(), IamAuthorities.PROJECT_PHASE_ARCHIVE);

        if (phase.status() == ProjectPhaseStatus.ARCHIVED) {
            throw ProjectExceptions.projectPhaseAlreadyArchived(cmd.id());
        }

        if (projectPhaseRepository.hasActiveWbsNodesOrTasks(cmd.id())) {
            throw ProjectExceptions.projectPhaseCannotArchive(cmd.id());
        }

        var archived = phase.archive();
        var saved = projectPhaseRepository.save(archived);

        activityLogger.logSuccess(
                ProjectEntityTypes.PROJECT_PHASE,
                saved.id(),
                ProjectActivityActions.ARCHIVE_PROJECT_PHASE,
                "Project phase archived: " + saved.code()
        );

        return ProjectPhaseResponse.from(saved);
    }
}

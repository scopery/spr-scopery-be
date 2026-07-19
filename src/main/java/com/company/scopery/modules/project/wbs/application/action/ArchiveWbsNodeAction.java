package com.company.scopery.modules.project.wbs.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.wbs.application.command.ArchiveWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.response.WbsNodeResponse;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArchiveWbsNodeAction {

    private final WbsNodeRepository wbsNodeRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;

    public ArchiveWbsNodeAction(WbsNodeRepository wbsNodeRepository,
                                ProjectActivityLogger activityLogger,
                                ProjectWorkspaceAuthorizationService authorizationService,
                                ProjectMutationGuard mutationGuard,
                                ProjectPlatformPublisher platformPublisher,
                                CurrentUserAuthorizationService currentUserAuthorizationService) {
        this.wbsNodeRepository = wbsNodeRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
        this.platformPublisher = platformPublisher;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
    }

    @Transactional
    public WbsNodeResponse execute(ArchiveWbsNodeCommand cmd) {
        WbsNode node = wbsNodeRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(cmd.id()));

        if (cmd.projectId() != null && !node.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.wbsNodeProjectMismatch(node.id(), cmd.projectId());
        }

        authorizationService.requireWbsArchive(node.projectId());
        Project project = mutationGuard.requireMutableProject(node.projectId());

        if (node.status() == WbsNodeStatus.ARCHIVED) {
            throw ProjectExceptions.wbsNodeAlreadyArchived(node.id());
        }

        if (wbsNodeRepository.hasActiveChildrenOrLinkedTasks(node.id())) {
            throw ProjectExceptions.wbsNodeCannotArchive(node.id());
        }

        WbsNode archived = node.archive();
        WbsNode saved = wbsNodeRepository.save(archived);

        var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        platformPublisher.enqueueWbs(saved, "WBS_NODE_ARCHIVED");
        platformPublisher.auditWbsArchived(actorId, saved, project.organizationId(), project.workspaceId());

        activityLogger.logSuccess(
                ProjectEntityTypes.WBS_NODE,
                saved.id(),
                ProjectActivityActions.ARCHIVE_WBS_NODE,
                "WBS node archived: " + saved.code()
        );

        return WbsNodeResponse.from(saved);
    }
}

package com.company.scopery.modules.project.wbs.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.wbs.application.command.DeleteWbsNodeCommand;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteWbsNodeAction {

    private final WbsNodeRepository wbsNodeRepository;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;

    public DeleteWbsNodeAction(WbsNodeRepository wbsNodeRepository,
                               ProjectWorkspaceAuthorizationService authorizationService,
                               ProjectMutationGuard mutationGuard,
                               ProjectActivityLogger activityLogger,
                               CurrentUserAuthorizationService currentUserAuthorizationService) {
        this.wbsNodeRepository = wbsNodeRepository;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
        this.activityLogger = activityLogger;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
    }

    @Transactional
    public void execute(DeleteWbsNodeCommand cmd) {
        WbsNode node = wbsNodeRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(cmd.id()));

        if (cmd.projectId() != null && !node.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.wbsNodeProjectMismatch(node.id(), cmd.projectId());
        }

        authorizationService.requireWbsArchive(node.projectId());
        mutationGuard.requireMutableProject(node.projectId());

        if (wbsNodeRepository.hasAnyChildrenOrLinkedTasks(node.id())) {
            throw ProjectExceptions.wbsNodeCannotDelete(node.id());
        }

        wbsNodeRepository.deleteById(node.id());

        var actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        activityLogger.logSuccess(
                ProjectEntityTypes.WBS_NODE,
                node.id(),
                ProjectActivityActions.ARCHIVE_WBS_NODE,
                "WBS node deleted: " + node.code()
        );
    }
}

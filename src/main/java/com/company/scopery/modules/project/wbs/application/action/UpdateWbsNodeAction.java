package com.company.scopery.modules.project.wbs.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.wbs.application.command.UpdateWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.response.WbsNodeResponse;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeType;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateWbsNodeAction {

    private final WbsNodeRepository wbsNodeRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;

    public UpdateWbsNodeAction(WbsNodeRepository wbsNodeRepository,
                               ProjectActivityLogger activityLogger,
                               ProjectWorkspaceAuthorizationService authorizationService,
                               ProjectMutationGuard mutationGuard,
                               ProjectPlatformPublisher platformPublisher) {
        this.wbsNodeRepository = wbsNodeRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
        this.mutationGuard = mutationGuard;
        this.platformPublisher = platformPublisher;
    }

    @Transactional
    public WbsNodeResponse execute(UpdateWbsNodeCommand cmd) {
        WbsNode node = wbsNodeRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(cmd.id()));

        if (cmd.projectId() != null && !node.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.wbsNodeProjectMismatch(node.id(), cmd.projectId());
        }

        authorizationService.requireWbsUpdate(node.projectId());
        mutationGuard.requireMutableProject(node.projectId());

        if (node.status() == WbsNodeStatus.ARCHIVED) {
            throw ProjectExceptions.wbsNodeAlreadyArchived(node.id());
        }

        WbsNodeType nodeType = ProjectEnumParser.parseRequired(
                WbsNodeType.class, cmd.nodeType(), "WBS_NODE_INVALID_TYPE", "nodeType");

        WbsNode updated = node.update(cmd.title(), cmd.description(), nodeType);
        WbsNode saved = wbsNodeRepository.save(updated);

        platformPublisher.enqueueWbs(saved, "WBS_NODE_UPDATED");

        activityLogger.logSuccess(
                ProjectEntityTypes.WBS_NODE,
                saved.id(),
                ProjectActivityActions.UPDATE_WBS_NODE,
                "WBS node updated: " + saved.code()
        );

        return WbsNodeResponse.from(saved);
    }
}

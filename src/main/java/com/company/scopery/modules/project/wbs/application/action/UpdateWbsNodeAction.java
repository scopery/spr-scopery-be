package com.company.scopery.modules.project.wbs.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
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

    public UpdateWbsNodeAction(WbsNodeRepository wbsNodeRepository,
                                ProjectActivityLogger activityLogger,
                                ProjectWorkspaceAuthorizationService authorizationService) {
        this.wbsNodeRepository = wbsNodeRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public WbsNodeResponse execute(UpdateWbsNodeCommand cmd) {
        WbsNode node = wbsNodeRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(cmd.id()));

        authorizationService.requireProjectPermission(node.projectId(), IamAuthorities.PROJECT_WBS_UPDATE);

        if (node.status() == WbsNodeStatus.ARCHIVED) {
            throw ProjectExceptions.wbsNodeAlreadyArchived(node.id());
        }

        WbsNodeType nodeType = ProjectEnumParser.parseRequired(
                WbsNodeType.class, cmd.nodeType(), "WBS_NODE_INVALID_TYPE", "nodeType");

        WbsNode updated = node.update(cmd.title(), cmd.description(), nodeType);
        WbsNode saved = wbsNodeRepository.save(updated);

        activityLogger.logSuccess(
                ProjectEntityTypes.WBS_NODE,
                saved.id(),
                ProjectActivityActions.UPDATE_WBS_NODE,
                "WBS node updated: " + saved.code()
        );

        return WbsNodeResponse.from(saved);
    }
}

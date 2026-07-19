package com.company.scopery.modules.project.wbs.application.action;

import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.support.ProjectMutationGuard;
import com.company.scopery.modules.project.shared.support.ProjectPlatformPublisher;
import com.company.scopery.modules.project.wbs.application.command.MoveWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.response.WbsNodeResponse;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeStatus;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MoveWbsNodeAction {

    private final WbsNodeRepository wbsNodeRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;
    private final ProjectMutationGuard mutationGuard;
    private final ProjectPlatformPublisher platformPublisher;

    public MoveWbsNodeAction(WbsNodeRepository wbsNodeRepository,
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
    public WbsNodeResponse execute(MoveWbsNodeCommand cmd) {
        authorizationService.requireWbsMove(cmd.projectId());
        mutationGuard.requireMutableProject(cmd.projectId());

        WbsNode node = wbsNodeRepository.findById(cmd.id())
                .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(cmd.id()));

        if (!node.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.wbsNodeProjectMismatch(node.id(), cmd.projectId());
        }

        if (node.status() == WbsNodeStatus.ARCHIVED) {
            throw ProjectExceptions.wbsNodeAlreadyArchived(node.id());
        }

        WbsNode newParent = null;
        if (cmd.newParentId() != null) {
            newParent = wbsNodeRepository.findById(cmd.newParentId())
                    .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(cmd.newParentId()));

            if (!newParent.projectId().equals(cmd.projectId())) {
                throw ProjectExceptions.wbsNodeProjectMismatch(newParent.id(), cmd.projectId());
            }

            if (!newParent.projectPhaseId().equals(node.projectPhaseId())) {
                throw ProjectExceptions.wbsNodePhaseMismatch(newParent.id(), node.projectPhaseId());
            }

            checkCircular(node.id(), newParent);
        }

        if (wbsNodeRepository.existsBySortOrderUnderParent(cmd.projectId(), cmd.newParentId(), cmd.newSortOrder())) {
            throw ProjectExceptions.wbsNodeSortOrderConflict(cmd.newSortOrder());
        }

        String oldPathPrefix = node.path();
        String newPath = newParent == null ? node.code() : newParent.path() + "/" + node.code();
        int newLevel = newParent == null ? 1 : newParent.level() + 1;

        WbsNode moved = wbsNodeRepository.save(node.move(cmd.newParentId(), cmd.newSortOrder(), newLevel, newPath));

        List<WbsNode> descendants = wbsNodeRepository.findAllDescendants(node.id());
        for (WbsNode desc : descendants) {
            String updatedPath = newPath + desc.path().substring(oldPathPrefix.length());
            int updatedLevel = newLevel + desc.level() - node.level();
            wbsNodeRepository.save(desc.withPath(updatedPath, updatedLevel));
        }

        platformPublisher.enqueueWbs(moved, "WBS_NODE_MOVED");

        activityLogger.logSuccess(
                ProjectEntityTypes.WBS_NODE,
                moved.id(),
                ProjectActivityActions.MOVE_WBS_NODE,
                "WBS node moved: " + moved.code()
        );

        return WbsNodeResponse.from(moved);
    }

    private void checkCircular(UUID nodeId, WbsNode startParent) {
        WbsNode current = startParent;
        int depth = 0;
        while (current != null && depth < 20) {
            if (nodeId.equals(current.id())) {
                throw ProjectExceptions.wbsNodeCircularParent();
            }
            UUID parentId = current.parentId();
            if (parentId == null) break;
            Optional<WbsNode> next = wbsNodeRepository.findById(parentId);
            current = next.orElse(null);
            depth++;
        }
    }
}

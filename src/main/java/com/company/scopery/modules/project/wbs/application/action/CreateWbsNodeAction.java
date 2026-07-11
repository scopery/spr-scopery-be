package com.company.scopery.modules.project.wbs.application.action;

import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.projectphase.domain.enums.ProjectPhaseStatus;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhase;
import com.company.scopery.modules.project.projectphase.domain.model.ProjectPhaseRepository;
import com.company.scopery.modules.project.shared.activity.ProjectActivityLogger;
import com.company.scopery.modules.project.shared.authorization.ProjectWorkspaceAuthorizationService;
import com.company.scopery.modules.project.shared.constant.ProjectActivityActions;
import com.company.scopery.modules.project.shared.constant.ProjectEntityTypes;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.project.shared.util.ProjectEnumParser;
import com.company.scopery.modules.project.wbs.application.command.CreateWbsNodeCommand;
import com.company.scopery.modules.project.wbs.application.response.WbsNodeResponse;
import com.company.scopery.modules.project.wbs.domain.enums.WbsNodeType;
import com.company.scopery.modules.project.wbs.domain.model.WbsNode;
import com.company.scopery.modules.project.wbs.domain.model.WbsNodeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateWbsNodeAction {

    private final WbsNodeRepository wbsNodeRepository;
    private final ProjectRepository projectRepository;
    private final ProjectPhaseRepository projectPhaseRepository;
    private final ProjectActivityLogger activityLogger;
    private final ProjectWorkspaceAuthorizationService authorizationService;

    public CreateWbsNodeAction(WbsNodeRepository wbsNodeRepository,
                                ProjectRepository projectRepository,
                                ProjectPhaseRepository projectPhaseRepository,
                                ProjectActivityLogger activityLogger,
                                ProjectWorkspaceAuthorizationService authorizationService) {
        this.wbsNodeRepository = wbsNodeRepository;
        this.projectRepository = projectRepository;
        this.projectPhaseRepository = projectPhaseRepository;
        this.activityLogger = activityLogger;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public WbsNodeResponse execute(CreateWbsNodeCommand cmd) {
        authorizationService.requireProjectPermission(cmd.projectId(), IamAuthorities.PROJECT_WBS_CREATE);

        Project project = projectRepository.findById(cmd.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(cmd.projectId()));

        if (project.status() != ProjectStatus.DRAFT && project.status() != ProjectStatus.ACTIVE) {
            throw ProjectExceptions.projectNotActiveOrDraft(project.id());
        }

        ProjectPhase phase = projectPhaseRepository.findById(cmd.projectPhaseId())
                .orElseThrow(() -> ProjectExceptions.projectPhaseNotFound(cmd.projectPhaseId()));

        if (!phase.projectId().equals(cmd.projectId())) {
            throw ProjectExceptions.wbsNodePhaseMismatch(cmd.projectPhaseId(), cmd.projectId());
        }

        if (phase.status() != ProjectPhaseStatus.PLANNED && phase.status() != ProjectPhaseStatus.ACTIVE) {
            throw ProjectExceptions.projectPhaseNotActive(phase.id());
        }

        WbsNode parent = null;
        if (cmd.parentId() != null) {
            parent = wbsNodeRepository.findById(cmd.parentId())
                    .orElseThrow(() -> ProjectExceptions.wbsNodeNotFound(cmd.parentId()));

            if (!parent.projectId().equals(cmd.projectId())) {
                throw ProjectExceptions.wbsNodeProjectMismatch(parent.id(), cmd.projectId());
            }

            if (!parent.projectPhaseId().equals(cmd.projectPhaseId())) {
                throw ProjectExceptions.wbsNodePhaseMismatch(parent.id(), cmd.projectPhaseId());
            }
        }

        if (wbsNodeRepository.existsByProjectIdAndCode(cmd.projectId(), cmd.code())) {
            throw ProjectExceptions.wbsNodeCodeAlreadyExists(cmd.code(), cmd.projectId());
        }

        if (wbsNodeRepository.existsBySortOrderUnderParent(cmd.projectId(), cmd.parentId(), cmd.sortOrder())) {
            throw ProjectExceptions.wbsNodeSortOrderConflict(cmd.sortOrder());
        }

        WbsNodeType nodeType = ProjectEnumParser.parseRequired(
                WbsNodeType.class, cmd.nodeType(), "WBS_NODE_INVALID_TYPE", "nodeType");

        int level;
        String path;
        if (parent == null) {
            level = 1;
            path = cmd.code();
        } else {
            level = parent.level() + 1;
            path = parent.path() + "/" + cmd.code();
        }

        WbsNode node = WbsNode.create(
                cmd.projectId(),
                cmd.projectPhaseId(),
                cmd.parentId(),
                cmd.code(),
                cmd.title(),
                cmd.description(),
                nodeType,
                level,
                path,
                cmd.sortOrder()
        );

        WbsNode saved = wbsNodeRepository.save(node);

        activityLogger.logSuccess(
                ProjectEntityTypes.WBS_NODE,
                saved.id(),
                ProjectActivityActions.CREATE_WBS_NODE,
                "WBS node created: " + saved.code()
        );

        return WbsNodeResponse.from(saved);
    }
}

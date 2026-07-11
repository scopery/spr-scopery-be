package com.company.scopery.modules.workspace.context.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.context.application.command.SwitchWorkspaceCommand;
import com.company.scopery.modules.workspace.context.application.response.WorkspaceContextResponse;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContext;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContextRepository;
import com.company.scopery.modules.workspace.access.application.service.WorkspaceAccessQueryService;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class SwitchWorkspaceAction {

    private final WorkspaceUserContextRepository contextRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceAccessQueryService accessQueryService;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceActivityLogger activityLogger;

    public SwitchWorkspaceAction(WorkspaceUserContextRepository contextRepository,
                                  WorkspaceRepository workspaceRepository,
                                  WorkspaceAccessQueryService accessQueryService,
                                  CurrentUserAuthorizationService currentUserService,
                                  WorkspaceActivityLogger activityLogger) {
        this.contextRepository = contextRepository;
        this.workspaceRepository = workspaceRepository;
        this.accessQueryService = accessQueryService;
        this.currentUserService = currentUserService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceContextResponse execute(SwitchWorkspaceCommand command) {
        UUID userId = currentUserService.resolveCurrentUser().id();

        Workspace ws = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(command.workspaceId()));

        if (ws.status() != WorkspaceStatus.ACTIVE) {
            throw WorkspaceExceptions.workspaceNotActive(ws.code().value());
        }
        if (!accessQueryService.hasEffectiveAccess(userId, command.workspaceId())) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_CONTEXT_NOT_MEMBER,
                    "You are not an active member of the selected workspace", null);
        }

        WorkspaceUserContext ctx = contextRepository.findByUserId(userId)
                .orElseGet(() -> WorkspaceUserContext.create(userId));
        WorkspaceUserContext updated = ctx.switchTo(command.workspaceId());
        WorkspaceUserContext saved = contextRepository.save(updated);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_USER_CONTEXT, command.workspaceId(),
                WorkspaceActivityActions.SWITCH_WORKSPACE, "Switched to workspace: " + ws.code().value());

        return buildResponse(saved);
    }

    private WorkspaceContextResponse buildResponse(WorkspaceUserContext ctx) {
        String workspaceName = null;
        String workspaceCode = null;
        if (ctx.currentWorkspaceId() != null) {
            var ws = workspaceRepository.findById(ctx.currentWorkspaceId()).orElse(null);
            if (ws != null) {
                workspaceName = ws.name();
                workspaceCode = ws.code().value();
            }
        }
        return new WorkspaceContextResponse(
                ctx.userId(), ctx.currentWorkspaceId(), workspaceName, workspaceCode,
                ctx.lastSwitchedAt(), ctx.onboardingCompletedAt() != null);
    }
}

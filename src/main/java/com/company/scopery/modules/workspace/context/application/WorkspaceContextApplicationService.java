package com.company.scopery.modules.workspace.context.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.context.application.response.WorkspaceContextResponse;
import com.company.scopery.modules.workspace.context.domain.WorkspaceUserContext;
import com.company.scopery.modules.workspace.context.domain.WorkspaceUserContextRepository;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceResponse;
import com.company.scopery.modules.workspace.workspace.domain.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceContextApplicationService {

    private final WorkspaceUserContextRepository contextRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceActivityLogger activityLogger;

    public WorkspaceContextApplicationService(
            WorkspaceUserContextRepository contextRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository memberRepository,
            CurrentUserAuthorizationService currentUserService,
            WorkspaceActivityLogger activityLogger) {
        this.contextRepository = contextRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
        this.currentUserService = currentUserService;
        this.activityLogger = activityLogger;
    }

    @Transactional(readOnly = true)
    public WorkspaceContextResponse getCurrentContext() {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceUserContext ctx = contextRepository.findByUserId(userId)
                .orElseGet(() -> WorkspaceUserContext.create(userId));
        return buildResponse(ctx);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getAvailableWorkspaces() {
        UUID userId = currentUserService.resolveCurrentUser().id();
        return workspaceRepository.findActiveByMemberId(userId)
                .stream().map(WorkspaceResponse::from).toList();
    }

    @Transactional
    public WorkspaceContextResponse switchWorkspace(UUID workspaceId) {
        UUID userId = currentUserService.resolveCurrentUser().id();

        Workspace ws = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(workspaceId));

        if (ws.status() != WorkspaceStatus.ACTIVE) {
            throw WorkspaceExceptions.workspaceNotActive(ws.code().value());
        }
        if (!memberRepository.isActiveMember(workspaceId, userId)) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_CONTEXT_NOT_MEMBER,
                    "You are not an active member of the selected workspace", null);
        }

        WorkspaceUserContext ctx = contextRepository.findByUserId(userId)
                .orElseGet(() -> WorkspaceUserContext.create(userId));
        WorkspaceUserContext updated = ctx.switchTo(workspaceId);
        WorkspaceUserContext saved = contextRepository.save(updated);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_USER_CONTEXT, workspaceId,
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

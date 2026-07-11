package com.company.scopery.modules.workspace.context.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.context.application.response.WorkspaceContextResponse;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContext;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContextRepository;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceResponse;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.access.application.service.WorkspaceAccessQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceContextQueryService {

    private final WorkspaceUserContextRepository contextRepository;
    private final WorkspaceRepository workspaceRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceAccessQueryService accessQueryService;
    private final boolean useEffectiveAccess;

    public WorkspaceContextQueryService(WorkspaceUserContextRepository contextRepository,
                                         WorkspaceRepository workspaceRepository,
                                         CurrentUserAuthorizationService currentUserService,
                                         WorkspaceAccessQueryService accessQueryService,
                                         @Value("${scopery.workspace.use-effective-access:true}") boolean useEffectiveAccess) {
        this.contextRepository = contextRepository;
        this.workspaceRepository = workspaceRepository;
        this.currentUserService = currentUserService;
        this.accessQueryService = accessQueryService;
        this.useEffectiveAccess = useEffectiveAccess;
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
        if (useEffectiveAccess) {
            return accessQueryService.findWorkspacesForUser(userId).stream()
                    .map(entry -> workspaceRepository.findById(entry.workspaceId())
                            .orElseThrow(() -> new IllegalStateException("Workspace IAM resource is orphaned: " + entry.workspaceId())))
                    .map(WorkspaceResponse::from)
                    .toList();
        }
        return workspaceRepository.findActiveByMemberId(userId)
                .stream().map(WorkspaceResponse::from).toList();
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

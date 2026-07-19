package com.company.scopery.modules.workspace.workspace.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.resource.application.service.IamAuthResourceLifecycleService;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceResponse;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveWorkspaceAction {

    private final WorkspaceRepository workspaceRepository;
    private final IamAuthResourceLifecycleService authResourceLifecycleService;
    private final WorkspaceActivityLogger activityLogger;
    private final ImmutableAuditEventService auditEventService;
    private final CurrentUserAuthorizationService currentUserService;

    public ArchiveWorkspaceAction(WorkspaceRepository workspaceRepository,
                                   IamAuthResourceLifecycleService authResourceLifecycleService,
                                   WorkspaceActivityLogger activityLogger,
                                   ImmutableAuditEventService auditEventService,
                                   CurrentUserAuthorizationService currentUserService) {
        this.workspaceRepository = workspaceRepository;
        this.authResourceLifecycleService = authResourceLifecycleService;
        this.activityLogger = activityLogger;
        this.auditEventService = auditEventService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public WorkspaceResponse execute(UUID id) {
        Workspace ws = findOrThrow(id);
        Workspace archived = ws.archive();
        Workspace saved = workspaceRepository.save(archived);
        authResourceLifecycleService.deactivateByRef(saved.id(), IamResourceType.WORKSPACE);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE, saved.id(),
                WorkspaceActivityActions.ARCHIVE_WORKSPACE,
                "Workspace archived: " + saved.code().value());

        UUID actorId = currentUserService.resolveCurrentUser().id();
        auditEventService.record(AuditEventType.WORKSPACE_ARCHIVED, actorId, "USER",
                "WORKSPACE", saved.id(), saved.organizationId(), saved.id(), null,
                java.util.Map.of("status", saved.status().name()), "Workspace archived");

        return WorkspaceResponse.from(saved);
    }

    private Workspace findOrThrow(UUID id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(id));
    }
}

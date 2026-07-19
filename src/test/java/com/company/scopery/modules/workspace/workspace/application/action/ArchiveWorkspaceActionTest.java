package com.company.scopery.modules.workspace.workspace.application.action;

import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.resource.application.service.IamAuthResourceLifecycleService;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArchiveWorkspaceActionTest {

    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private IamAuthResourceLifecycleService authResourceLifecycleService;
    @Mock private WorkspaceActivityLogger activityLogger;
    @Mock private ImmutableAuditEventService auditEventService;
    @Mock private CurrentUserAuthorizationService currentUserService;

    private ArchiveWorkspaceAction action;
    private UUID workspaceId;

    @BeforeEach
    void setUp() {
        action = new ArchiveWorkspaceAction(workspaceRepository, authResourceLifecycleService,
                activityLogger, auditEventService, currentUserService);
        workspaceId = UUID.randomUUID();
        Instant now = Instant.now();
        when(currentUserService.resolveCurrentUser()).thenReturn(
                IamUser.of(UUID.randomUUID(), Username.of("admin"), EmailAddress.of("a@example.com"),
                        "Admin", null, IamUserStatus.ACTIVE, now, now));
    }

    @Test
    void archiveWorkspace_deactivatesIamAuthResource() {
        Workspace ws = new Workspace(workspaceId, UUID.randomUUID(), WorkspaceCode.of("WS1"), "WS",
                null, null,
                com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceVisibility.PRIVATE,
                com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy.INVITE_ONLY,
                WorkspaceStatus.ACTIVE, 0, Instant.now(), Instant.now());
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(ws));
        when(workspaceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        action.execute(workspaceId);

        verify(authResourceLifecycleService).deactivateByRef(workspaceId, IamResourceType.WORKSPACE);
        verify(auditEventService).record(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }
}

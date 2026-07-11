package com.company.scopery.modules.workspace.workspace.application;
import com.company.scopery.modules.workspace.workspace.application.action.CreateWorkspaceAction;
import com.company.scopery.modules.workspace.workspace.application.action.UpdateWorkspaceAction;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.valueobject.OrganizationCode;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.enums.OrganizationStatus;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.workspace.application.command.CreateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.command.UpdateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceDetailResponse;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceVisibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceActionTest {

    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private WorkspaceActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService iamIntegrationService;
    @Mock private ImmutableAuditEventService auditEventService;
    @Mock private TransactionalOutboxService outboxService;

    private IamUser currentUser;

    private CreateWorkspaceAction createWorkspaceAction;
    private UpdateWorkspaceAction updateWorkspaceAction;

    @BeforeEach
    void setUp() {
        createWorkspaceAction = new CreateWorkspaceAction(workspaceRepository, organizationRepository, workspaceMemberRepository, activityLogger, currentUserService, iamIntegrationService, auditEventService, outboxService);
        updateWorkspaceAction = new UpdateWorkspaceAction(workspaceRepository, activityLogger);
        Instant now = Instant.now();
        currentUser = new IamUser(UUID.randomUUID(), Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin User", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void createWorkspace_success_ownerMemberBootstrapped() {
        UUID orgId = UUID.randomUUID();
        CreateWorkspaceCommand command = new CreateWorkspaceCommand(orgId, "Dev Workspace", "DEV_WS", "Development", null, null);

        Organization org = activeOrganization(orgId);
        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(org));
        when(workspaceRepository.existsByOrganizationIdAndCode(any(), any())).thenReturn(false);
        when(workspaceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(workspaceMemberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(iamIntegrationService.bootstrapWorkspaceAccess(any(), any(), any(), any())).thenReturn(UUID.randomUUID());

        WorkspaceDetailResponse response = createWorkspaceAction.execute(command);

        assertThat(response.code()).isEqualTo("DEV_WS");
        assertThat(response.status()).isEqualTo("ACTIVE");
        verify(workspaceRepository).save(any(Workspace.class));
        verify(workspaceMemberRepository).save(any(WorkspaceMember.class));
        verify(activityLogger).logSuccess(eq("WORKSPACE"), any(UUID.class),
                eq("CREATE_WORKSPACE"), any(String.class));
    }

    @Test
    void createWorkspace_defaultVisibilityIsPrivate_whenNotSpecified() {
        UUID orgId = UUID.randomUUID();
        CreateWorkspaceCommand command = new CreateWorkspaceCommand(orgId, "Dev Workspace", "DEV_WS", null, null, null);

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(activeOrganization(orgId)));
        when(workspaceRepository.existsByOrganizationIdAndCode(any(), any())).thenReturn(false);
        when(workspaceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(workspaceMemberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(iamIntegrationService.bootstrapWorkspaceAccess(any(), any(), any(), any())).thenReturn(UUID.randomUUID());

        WorkspaceDetailResponse response = createWorkspaceAction.execute(command);

        assertThat(response.defaultVisibility()).isEqualTo("PRIVATE");
    }

    @Test
    void createWorkspace_duplicateCode_throwsConflict() {
        UUID orgId = UUID.randomUUID();
        CreateWorkspaceCommand command = new CreateWorkspaceCommand(orgId, "Dev Workspace", "DEV_WS", null, null, null);

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(activeOrganization(orgId)));
        when(workspaceRepository.existsByOrganizationIdAndCode(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> createWorkspaceAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            WorkspaceErrorCatalog.WORKSPACE_CODE_ALREADY_EXISTS.code());
                });

        verify(workspaceRepository, never()).save(any());
    }

    @Test
    void createWorkspace_inactiveOrganization_throwsUnprocessable() {
        UUID orgId = UUID.randomUUID();
        Organization inactiveOrg = inactiveOrganization(orgId);
        CreateWorkspaceCommand command = new CreateWorkspaceCommand(orgId, "Dev Workspace", "DEV_WS", null, null, null);

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(inactiveOrg));

        assertThatThrownBy(() -> createWorkspaceAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            WorkspaceErrorCatalog.ORGANIZATION_NOT_ACTIVE.code());
                });
    }

    @Test
    void updateWorkspace_archivedWorkspace_throwsUnprocessable() {
        UUID id = UUID.randomUUID();
        Workspace archived = existingWorkspace(id, UUID.randomUUID(), WorkspaceStatus.ARCHIVED);
        UpdateWorkspaceCommand command = new UpdateWorkspaceCommand(id, "New Name", null, null, null);

        when(workspaceRepository.findById(id)).thenReturn(Optional.of(archived));

        assertThatThrownBy(() -> updateWorkspaceAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            WorkspaceErrorCatalog.WORKSPACE_ARCHIVED_CANNOT_BE_UPDATED.code());
                });

        verify(workspaceRepository, never()).save(any());
    }

    private Organization activeOrganization(UUID id) {
        Instant now = Instant.now();
        return new Organization(id, OrganizationCode.of("ACME"), "Acme Corp", null,
                UUID.randomUUID(), OrganizationStatus.ACTIVE, 0, now, now);
    }

    private Organization inactiveOrganization(UUID id) {
        Instant now = Instant.now();
        return new Organization(id, OrganizationCode.of("ACME"), "Acme Corp", null,
                UUID.randomUUID(), OrganizationStatus.INACTIVE, 0, now, now);
    }

    private Workspace existingWorkspace(UUID id, UUID orgId, WorkspaceStatus status) {
        Instant now = Instant.now();
        return new Workspace(id, orgId, WorkspaceCode.of("DEV_WS"), "Dev Workspace", null,
                UUID.randomUUID(), WorkspaceVisibility.PRIVATE, WorkspaceJoinPolicy.INVITE_ONLY, status, 0, now, now);
    }
}

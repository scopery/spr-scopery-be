package com.company.scopery.modules.workspace.workspace.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.integration.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.organization.domain.Organization;
import com.company.scopery.modules.workspace.organization.domain.OrganizationCode;
import com.company.scopery.modules.workspace.organization.domain.OrganizationRepository;
import com.company.scopery.modules.workspace.organization.domain.OrganizationStatus;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.workspace.application.command.CreateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.command.UpdateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceDetailResponse;
import com.company.scopery.modules.workspace.workspace.domain.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceCode;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceVisibility;
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
class WorkspaceApplicationServiceTest {

    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private WorkspaceActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService iamIntegrationService;

    private WorkspaceApplicationService service;
    private IamUser currentUser;

    @BeforeEach
    void setUp() {
        service = new WorkspaceApplicationService(
                workspaceRepository, organizationRepository, workspaceMemberRepository,
                activityLogger, currentUserService, iamIntegrationService);
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
        when(iamIntegrationService.bootstrapWorkspaceAccess(any(), any(), any())).thenReturn(UUID.randomUUID());

        WorkspaceDetailResponse response = service.createWorkspace(command);

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
        when(iamIntegrationService.bootstrapWorkspaceAccess(any(), any(), any())).thenReturn(UUID.randomUUID());

        WorkspaceDetailResponse response = service.createWorkspace(command);

        assertThat(response.defaultVisibility()).isEqualTo("PRIVATE");
    }

    @Test
    void createWorkspace_duplicateCode_throwsConflict() {
        UUID orgId = UUID.randomUUID();
        CreateWorkspaceCommand command = new CreateWorkspaceCommand(orgId, "Dev Workspace", "DEV_WS", null, null, null);

        when(organizationRepository.findById(orgId)).thenReturn(Optional.of(activeOrganization(orgId)));
        when(workspaceRepository.existsByOrganizationIdAndCode(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.createWorkspace(command))
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

        assertThatThrownBy(() -> service.createWorkspace(command))
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

        assertThatThrownBy(() -> service.updateWorkspace(command))
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
                UUID.randomUUID(), OrganizationStatus.ACTIVE, now, now);
    }

    private Organization inactiveOrganization(UUID id) {
        Instant now = Instant.now();
        return new Organization(id, OrganizationCode.of("ACME"), "Acme Corp", null,
                UUID.randomUUID(), OrganizationStatus.INACTIVE, now, now);
    }

    private Workspace existingWorkspace(UUID id, UUID orgId, WorkspaceStatus status) {
        Instant now = Instant.now();
        return new Workspace(id, orgId, WorkspaceCode.of("DEV_WS"), "Dev Workspace", null,
                UUID.randomUUID(), WorkspaceVisibility.PRIVATE, WorkspaceJoinPolicy.INVITE_ONLY, status, now, now);
    }
}

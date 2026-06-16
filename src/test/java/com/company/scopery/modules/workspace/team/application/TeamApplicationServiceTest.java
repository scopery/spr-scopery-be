package com.company.scopery.modules.workspace.team.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.integration.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.team.application.command.CreateTeamCommand;
import com.company.scopery.modules.workspace.team.application.command.UpdateTeamCommand;
import com.company.scopery.modules.workspace.team.application.response.TeamResponse;
import com.company.scopery.modules.workspace.team.domain.TeamCode;
import com.company.scopery.modules.workspace.team.domain.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.WorkspaceTeam;
import com.company.scopery.modules.workspace.workspace.domain.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceCode;
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
class TeamApplicationServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private WorkspaceActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService iamIntegrationService;

    private TeamApplicationService service;
    private IamUser currentUser;

    @BeforeEach
    void setUp() {
        service = new TeamApplicationService(
                teamRepository, workspaceRepository, activityLogger, currentUserService, iamIntegrationService);
        Instant now = Instant.now();
        currentUser = new IamUser(UUID.randomUUID(), Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin User", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void createTeam_success_returnsTeamResponse() {
        UUID workspaceId = UUID.randomUUID();
        CreateTeamCommand command = new CreateTeamCommand(workspaceId, "Backend Team", "BACKEND", "Backend devs");

        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(activeWorkspace(workspaceId)));
        when(teamRepository.existsByWorkspaceIdAndCode(any(), any())).thenReturn(false);
        when(teamRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(iamIntegrationService.bootstrapTeamAccess(any(), any(), any(), any())).thenReturn(UUID.randomUUID());

        TeamResponse response = service.createTeam(command);

        assertThat(response.code()).isEqualTo("BACKEND");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.workspaceId()).isEqualTo(workspaceId);
        verify(teamRepository).save(any(WorkspaceTeam.class));
        verify(activityLogger).logSuccess(eq("TEAM"), any(UUID.class),
                eq("CREATE_TEAM"), any(String.class));
    }

    @Test
    void createTeam_duplicateCode_throwsConflict() {
        UUID workspaceId = UUID.randomUUID();
        CreateTeamCommand command = new CreateTeamCommand(workspaceId, "Backend Team", "BACKEND", null);

        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(activeWorkspace(workspaceId)));
        when(teamRepository.existsByWorkspaceIdAndCode(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.createTeam(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(WorkspaceErrorCatalog.TEAM_CODE_ALREADY_EXISTS.code());
                });

        verify(teamRepository, never()).save(any());
    }

    @Test
    void updateTeam_archivedTeam_throwsUnprocessable() {
        UUID workspaceId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        WorkspaceTeam archived = archivedTeam(teamId, workspaceId);
        UpdateTeamCommand command = new UpdateTeamCommand(workspaceId, teamId, "New Name", null);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(archived));

        assertThatThrownBy(() -> service.updateTeam(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            WorkspaceErrorCatalog.TEAM_ARCHIVED_CANNOT_BE_UPDATED.code());
                });

        verify(teamRepository, never()).save(any());
    }

    private Workspace activeWorkspace(UUID id) {
        Instant now = Instant.now();
        return new Workspace(id, UUID.randomUUID(), WorkspaceCode.of("DEV_WS"), "Dev Workspace", null,
                UUID.randomUUID(), WorkspaceVisibility.PRIVATE, WorkspaceStatus.ACTIVE, now, now);
    }

    private WorkspaceTeam archivedTeam(UUID teamId, UUID workspaceId) {
        Instant now = Instant.now();
        return new WorkspaceTeam(teamId, workspaceId, TeamCode.of("BACKEND"), "Backend Team", null,
                TeamStatus.ARCHIVED, now, now);
    }
}

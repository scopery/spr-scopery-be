package com.company.scopery.modules.workspace.team.application;
import com.company.scopery.modules.workspace.team.application.action.CreateTeamAction;
import com.company.scopery.modules.workspace.team.application.action.UpdateTeamAction;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.team.application.command.CreateTeamCommand;
import com.company.scopery.modules.workspace.team.application.command.UpdateTeamCommand;
import com.company.scopery.modules.workspace.team.application.response.TeamResponse;
import com.company.scopery.modules.workspace.team.domain.valueobject.TeamCode;
import com.company.scopery.modules.workspace.team.domain.model.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.enums.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeam;
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
class TeamActionTest {

    @Mock private TeamRepository teamRepository;
    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private WorkspaceActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService iamIntegrationService;

    private IamUser currentUser;

    private CreateTeamAction createTeamAction;
    private UpdateTeamAction updateTeamAction;

    @BeforeEach
    void setUp() {
        createTeamAction = new CreateTeamAction(teamRepository, workspaceRepository, activityLogger, currentUserService, iamIntegrationService);
        updateTeamAction = new UpdateTeamAction(teamRepository, activityLogger);
        Instant now = Instant.now();
        currentUser = IamUser.of(UUID.randomUUID(), Username.of("admin"),
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

        TeamResponse response = createTeamAction.execute(command);

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

        assertThatThrownBy(() -> createTeamAction.execute(command))
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

        assertThatThrownBy(() -> updateTeamAction.execute(command))
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
                UUID.randomUUID(), WorkspaceVisibility.PRIVATE, WorkspaceJoinPolicy.INVITE_ONLY, WorkspaceStatus.ACTIVE, 0, now, now);
    }

    private WorkspaceTeam archivedTeam(UUID teamId, UUID workspaceId) {
        Instant now = Instant.now();
        return new WorkspaceTeam(teamId, workspaceId, TeamCode.of("BACKEND"), "Backend Team", null,
                TeamStatus.ARCHIVED, now, now);
    }
}

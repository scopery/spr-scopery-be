package com.company.scopery.modules.workspace.team.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.integration.WorkspaceIamIntegrationService;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.team.application.command.AddTeamMemberCommand;
import com.company.scopery.modules.workspace.team.application.command.RemoveTeamMemberCommand;
import com.company.scopery.modules.workspace.team.application.response.TeamMemberResponse;
import com.company.scopery.modules.workspace.team.domain.TeamCode;
import com.company.scopery.modules.workspace.team.domain.TeamMemberRepository;
import com.company.scopery.modules.workspace.team.domain.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.WorkspaceTeam;
import com.company.scopery.modules.workspace.team.domain.WorkspaceTeamMember;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceCode;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceVisibility;
import com.company.scopery.modules.workspace.workspace.domain.Workspace;
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
class TeamMemberApplicationServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock private WorkspaceActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService iamIntegrationService;

    private TeamApplicationService teamApplicationService;
    private TeamMemberApplicationService service;

    @BeforeEach
    void setUp() {
        teamApplicationService = new TeamApplicationService(
                teamRepository, workspaceRepository, activityLogger, currentUserService, iamIntegrationService);
        service = new TeamMemberApplicationService(
                teamApplicationService, teamMemberRepository, workspaceMemberRepository, activityLogger);
    }

    @Test
    void addTeamMember_success_returnsMemberResponse() {
        UUID workspaceId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AddTeamMemberCommand command = new AddTeamMemberCommand(workspaceId, teamId, userId);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(activeTeam(teamId, workspaceId)));
        when(workspaceMemberRepository.isActiveMember(workspaceId, userId)).thenReturn(true);
        when(teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)).thenReturn(false);
        when(teamMemberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TeamMemberResponse response = service.addTeamMember(command);

        assertThat(response.teamId()).isEqualTo(teamId);
        assertThat(response.userId()).isEqualTo(userId);
        verify(teamMemberRepository).save(any(WorkspaceTeamMember.class));
        verify(activityLogger).logSuccess(eq("TEAM_MEMBER"), any(UUID.class),
                eq("ADD_TEAM_MEMBER"), any(String.class));
    }

    @Test
    void addTeamMember_notWorkspaceMember_throwsUnprocessable() {
        UUID workspaceId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AddTeamMemberCommand command = new AddTeamMemberCommand(workspaceId, teamId, userId);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(activeTeam(teamId, workspaceId)));
        when(workspaceMemberRepository.isActiveMember(workspaceId, userId)).thenReturn(false);

        assertThatThrownBy(() -> service.addTeamMember(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            WorkspaceErrorCatalog.TEAM_MEMBER_REQUIRES_WORKSPACE_MEMBER.code());
                });
    }

    @Test
    void addTeamMember_alreadyExists_throwsConflict() {
        UUID workspaceId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AddTeamMemberCommand command = new AddTeamMemberCommand(workspaceId, teamId, userId);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(activeTeam(teamId, workspaceId)));
        when(workspaceMemberRepository.isActiveMember(workspaceId, userId)).thenReturn(true);
        when(teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)).thenReturn(true);

        assertThatThrownBy(() -> service.addTeamMember(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(WorkspaceErrorCatalog.TEAM_MEMBER_ALREADY_EXISTS.code());
                });
    }

    @Test
    void removeTeamMember_notFound_throwsNotFound() {
        UUID workspaceId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RemoveTeamMemberCommand command = new RemoveTeamMemberCommand(workspaceId, teamId, userId);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(activeTeam(teamId, workspaceId)));
        when(teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)).thenReturn(false);

        assertThatThrownBy(() -> service.removeTeamMember(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void removeTeamMember_success_deletesAndLogs() {
        UUID workspaceId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RemoveTeamMemberCommand command = new RemoveTeamMemberCommand(workspaceId, teamId, userId);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(activeTeam(teamId, workspaceId)));
        when(teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)).thenReturn(true);

        service.removeTeamMember(command);

        verify(teamMemberRepository).delete(teamId, userId);
        verify(activityLogger).logSuccess(eq("TEAM_MEMBER"), any(UUID.class),
                eq("REMOVE_TEAM_MEMBER"), any(String.class));
    }

    private WorkspaceTeam activeTeam(UUID teamId, UUID workspaceId) {
        Instant now = Instant.now();
        return new WorkspaceTeam(teamId, workspaceId, TeamCode.of("BACKEND"), "Backend Team", null,
                TeamStatus.ACTIVE, now, now);
    }

    @SuppressWarnings("unused")
    private Workspace activeWorkspace(UUID id) {
        Instant now = Instant.now();
        return new Workspace(id, UUID.randomUUID(), WorkspaceCode.of("DEV_WS"), "Dev Workspace", null,
                UUID.randomUUID(), WorkspaceVisibility.PRIVATE, WorkspaceStatus.ACTIVE, now, now);
    }
}

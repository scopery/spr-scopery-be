package com.company.scopery.modules.workspace.member.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.workspace.member.application.command.ActivateWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.command.AddWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceMemberResponse;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
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
class WorkspaceMemberApplicationServiceTest {

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceActivityLogger activityLogger;

    private WorkspaceMemberApplicationService service;

    @BeforeEach
    void setUp() {
        service = new WorkspaceMemberApplicationService(
                workspaceMemberRepository, workspaceRepository, activityLogger);
    }

    @Test
    void addMember_success_returnsMemberResponse() {
        UUID workspaceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AddWorkspaceMemberCommand command = new AddWorkspaceMemberCommand(workspaceId, userId);

        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(activeWorkspace(workspaceId)));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId)).thenReturn(false);
        when(workspaceMemberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceMemberResponse response = service.addMember(command);

        assertThat(response.workspaceId()).isEqualTo(workspaceId);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.status()).isEqualTo("ACTIVE");
        verify(workspaceMemberRepository).save(any(WorkspaceMember.class));
        verify(activityLogger).logSuccess(eq("WORKSPACE_MEMBER"), any(UUID.class),
                eq("ADD_WORKSPACE_MEMBER"), any(String.class));
    }

    @Test
    void addMember_alreadyExists_throwsConflict() {
        UUID workspaceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AddWorkspaceMemberCommand command = new AddWorkspaceMemberCommand(workspaceId, userId);

        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(activeWorkspace(workspaceId)));
        when(workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId)).thenReturn(true);

        assertThatThrownBy(() -> service.addMember(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            WorkspaceErrorCatalog.WORKSPACE_MEMBER_ALREADY_EXISTS.code());
                });

        verify(workspaceMemberRepository, never()).save(any());
    }

    @Test
    void addMember_inactiveWorkspace_throwsUnprocessable() {
        UUID workspaceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AddWorkspaceMemberCommand command = new AddWorkspaceMemberCommand(workspaceId, userId);

        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(inactiveWorkspace(workspaceId)));

        assertThatThrownBy(() -> service.addMember(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            WorkspaceErrorCatalog.WORKSPACE_NOT_ACTIVE.code());
                });
    }

    private Workspace activeWorkspace(UUID id) {
        Instant now = Instant.now();
        return new Workspace(id, UUID.randomUUID(), WorkspaceCode.of("DEV_WS"), "Dev Workspace", null,
                UUID.randomUUID(), WorkspaceVisibility.PRIVATE, WorkspaceStatus.ACTIVE, now, now);
    }

    private Workspace inactiveWorkspace(UUID id) {
        Instant now = Instant.now();
        return new Workspace(id, UUID.randomUUID(), WorkspaceCode.of("DEV_WS"), "Dev Workspace", null,
                UUID.randomUUID(), WorkspaceVisibility.PRIVATE, WorkspaceStatus.INACTIVE, now, now);
    }

    @Test
    void activateMember_inactiveWorkspace_throwsUnprocessable() {
        UUID workspaceId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        ActivateWorkspaceMemberCommand command = new ActivateWorkspaceMemberCommand(workspaceId, memberId);

        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(inactiveWorkspace(workspaceId)));

        assertThatThrownBy(() -> service.activateMember(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            WorkspaceErrorCatalog.WORKSPACE_MEMBER_CANNOT_BE_ACTIVATED_FOR_INACTIVE_WORKSPACE.code());
                });
    }

}

package com.company.scopery.modules.workspace.invitation.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.integration.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import com.company.scopery.modules.notification.emailtrigger.domain.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.workspace.invitation.application.command.CreateWorkspaceInvitationCommand;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
import com.company.scopery.modules.workspace.invitation.domain.InvitationCodeHasher;
import com.company.scopery.modules.workspace.invitation.domain.WorkspaceInvitation;
import com.company.scopery.modules.workspace.invitation.domain.WorkspaceInvitationRepository;
import com.company.scopery.modules.workspace.invitation.domain.WorkspaceInvitationStatus;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceInvitationApplicationServiceTest {

    @Mock private WorkspaceInvitationRepository invitationRepository;
    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private WorkspaceMemberRepository memberRepository;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService iamIntegrationService;
    @Mock private EmailNotificationTriggerPublisher notificationPublisher;
    @Mock private WorkspaceActivityLogger activityLogger;

    private WorkspaceInvitationApplicationService service;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        service = new WorkspaceInvitationApplicationService(
                invitationRepository, workspaceRepository, memberRepository,
                currentUserService, iamIntegrationService, notificationPublisher, activityLogger);
        Instant now = Instant.now();
        currentUserId = UUID.randomUUID();
        IamUser currentUser = new IamUser(currentUserId, Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin User", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void createInvitation_success_returnsRawCodeOnce() {
        UUID workspaceId = UUID.randomUUID();
        CreateWorkspaceInvitationCommand command = new CreateWorkspaceInvitationCommand(
                workspaceId, null, null, null, false);

        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(activeWorkspace(workspaceId)));
        when(invitationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceInvitationResponse response = service.createInvitation(command);

        assertThat(response.invitationCode()).isNotNull();
        assertThat(response.invitationCode()).hasSize(20);
        assertThat(response.invitationCodeHint()).hasSize(4);
        assertThat(response.status()).isEqualTo("PENDING");
        verify(invitationRepository).save(any(WorkspaceInvitation.class));
    }

    @Test
    void acceptInvitation_success_addsMember() {
        String rawCode = "TESTRAWCODE12345678A";
        UUID workspaceId = UUID.randomUUID();
        WorkspaceInvitation inv = buildPendingInvitation(workspaceId, rawCode, null, 0, null);

        when(invitationRepository.findByCodeHash(InvitationCodeHasher.hash(rawCode))).thenReturn(Optional.of(inv));
        when(memberRepository.isActiveMember(workspaceId, currentUserId)).thenReturn(false);
        when(invitationRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(memberRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.acceptInvitation(rawCode);

        verify(memberRepository).save(any(WorkspaceMember.class));
        verify(invitationRepository).save(any(WorkspaceInvitation.class));
    }

    @Test
    void acceptInvitation_hashMismatch_throws404() {
        when(invitationRepository.findByCodeHash(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.acceptInvitation("UNKNOWNCODE12345678A"))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void acceptInvitation_expired_throws422() {
        String rawCode = "TESTRAWCODE12345678B";
        UUID workspaceId = UUID.randomUUID();
        Instant pastExpiry = Instant.now().minusSeconds(3600);
        WorkspaceInvitation inv = buildPendingInvitation(workspaceId, rawCode, null, 0, pastExpiry);

        when(invitationRepository.findByCodeHash(InvitationCodeHasher.hash(rawCode))).thenReturn(Optional.of(inv));
        when(memberRepository.isActiveMember(workspaceId, currentUserId)).thenReturn(false);

        assertThatThrownBy(() -> service.acceptInvitation(rawCode))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(WorkspaceErrorCatalog.WORKSPACE_INVITATION_EXPIRED.code());
                });
    }

    @Test
    void acceptInvitation_revoked_throws422() {
        String rawCode = "TESTRAWCODE12345678C";
        UUID workspaceId = UUID.randomUUID();
        Instant now = Instant.now();
        WorkspaceInvitation inv = new WorkspaceInvitation(UUID.randomUUID(), workspaceId, UUID.randomUUID(), null,
                InvitationCodeHasher.hash(rawCode), InvitationCodeHasher.hint(rawCode),
                WorkspaceInvitationStatus.REVOKED, null, 0, null, now, now);

        when(invitationRepository.findByCodeHash(InvitationCodeHasher.hash(rawCode))).thenReturn(Optional.of(inv));
        when(memberRepository.isActiveMember(workspaceId, currentUserId)).thenReturn(false);

        assertThatThrownBy(() -> service.acceptInvitation(rawCode))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(WorkspaceErrorCatalog.WORKSPACE_INVITATION_REVOKED.code());
                });
    }

    @Test
    void acceptInvitation_maxUsesReached_throws422() {
        String rawCode = "TESTRAWCODE12345678D";
        UUID workspaceId = UUID.randomUUID();
        WorkspaceInvitation inv = buildPendingInvitation(workspaceId, rawCode, 1, 1, null);

        when(invitationRepository.findByCodeHash(InvitationCodeHasher.hash(rawCode))).thenReturn(Optional.of(inv));
        when(memberRepository.isActiveMember(workspaceId, currentUserId)).thenReturn(false);

        assertThatThrownBy(() -> service.acceptInvitation(rawCode))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(WorkspaceErrorCatalog.WORKSPACE_INVITATION_MAX_USES_REACHED.code());
                });
    }

    @Test
    void acceptInvitation_alreadyMember_throws409() {
        String rawCode = "TESTRAWCODE12345678E";
        UUID workspaceId = UUID.randomUUID();
        WorkspaceInvitation inv = buildPendingInvitation(workspaceId, rawCode, null, 0, null);

        when(invitationRepository.findByCodeHash(InvitationCodeHasher.hash(rawCode))).thenReturn(Optional.of(inv));
        when(memberRepository.isActiveMember(workspaceId, currentUserId)).thenReturn(true);

        assertThatThrownBy(() -> service.acceptInvitation(rawCode))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(WorkspaceErrorCatalog.WORKSPACE_INVITATION_ALREADY_MEMBER.code());
                });
    }

    @Test
    void revokeInvitation_success() {
        UUID invId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();
        Instant now = Instant.now();
        WorkspaceInvitation inv = new WorkspaceInvitation(invId, workspaceId, UUID.randomUUID(), null,
                "somehash", "TEST", WorkspaceInvitationStatus.PENDING, null, 0, null, now, now);

        when(invitationRepository.findById(invId)).thenReturn(Optional.of(inv));
        when(invitationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        WorkspaceInvitationResponse response = service.revokeInvitation(invId, workspaceId);

        assertThat(response.status()).isEqualTo("REVOKED");
        verify(invitationRepository).save(any(WorkspaceInvitation.class));
    }

    @Test
    void listInvitations_doesNotReturnRawCode() {
        UUID workspaceId = UUID.randomUUID();
        Instant now = Instant.now();
        WorkspaceInvitation inv = new WorkspaceInvitation(UUID.randomUUID(), workspaceId, UUID.randomUUID(), null,
                "somehash", "TEST", WorkspaceInvitationStatus.PENDING, null, 0, null, now, now);

        when(invitationRepository.findByWorkspaceId(workspaceId)).thenReturn(List.of(inv));

        List<WorkspaceInvitationResponse> responses = service.listInvitations(workspaceId);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).invitationCode()).isNull();
        assertThat(responses.get(0).invitationCodeHint()).isEqualTo("TEST");
    }

    private Workspace activeWorkspace(UUID id) {
        Instant now = Instant.now();
        return new Workspace(id, UUID.randomUUID(), WorkspaceCode.of("DEV_WS"), "Dev Workspace", null,
                UUID.randomUUID(), WorkspaceVisibility.PRIVATE, WorkspaceJoinPolicy.INVITE_ONLY,
                WorkspaceStatus.ACTIVE, now, now);
    }

    private WorkspaceInvitation buildPendingInvitation(UUID workspaceId, String rawCode,
                                                        Integer maxUses, int usedCount, Instant expiresAt) {
        Instant now = Instant.now();
        return new WorkspaceInvitation(UUID.randomUUID(), workspaceId, UUID.randomUUID(), null,
                InvitationCodeHasher.hash(rawCode), InvitationCodeHasher.hint(rawCode),
                WorkspaceInvitationStatus.PENDING, maxUses, usedCount, expiresAt, now, now);
    }
}

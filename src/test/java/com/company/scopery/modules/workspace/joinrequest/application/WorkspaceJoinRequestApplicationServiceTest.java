package com.company.scopery.modules.workspace.joinrequest.application;
import com.company.scopery.modules.workspace.joinrequest.application.action.ApproveJoinRequestAction;
import com.company.scopery.modules.workspace.joinrequest.application.action.CancelJoinRequestAction;
import com.company.scopery.modules.workspace.joinrequest.application.action.CreateJoinRequestAction;
import com.company.scopery.modules.workspace.joinrequest.application.action.RejectJoinRequestAction;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.workspace.joinrequest.application.command.CancelJoinRequestCommand;
import com.company.scopery.modules.workspace.joinrequest.application.command.CreateWorkspaceJoinRequestCommand;
import com.company.scopery.modules.workspace.joinrequest.application.command.ReviewWorkspaceJoinRequestCommand;
import com.company.scopery.modules.workspace.joinrequest.application.response.WorkspaceJoinRequestResponse;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequest;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequestRepository;
import com.company.scopery.modules.workspace.joinrequest.domain.enums.WorkspaceJoinRequestStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
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
class WorkspaceJoinRequestActionTest {

    @Mock private WorkspaceJoinRequestRepository joinRequestRepository;
    @Mock private WorkspaceRepository workspaceRepository;
    @Mock private WorkspaceMemberRepository memberRepository;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService iamIntegrationService;
    @Mock private EmailNotificationTriggerPublisher notificationPublisher;
    @Mock private WorkspaceActivityLogger activityLogger;

    private UUID currentUserId;

    private ApproveJoinRequestAction approveJoinRequestAction;
    private CancelJoinRequestAction cancelJoinRequestAction;
    private CreateJoinRequestAction createJoinRequestAction;
    private RejectJoinRequestAction rejectJoinRequestAction;

    @BeforeEach
    void setUp() {
        approveJoinRequestAction = new ApproveJoinRequestAction(joinRequestRepository, memberRepository, currentUserService, iamIntegrationService, notificationPublisher, activityLogger);
        cancelJoinRequestAction = new CancelJoinRequestAction(joinRequestRepository, currentUserService, activityLogger);
        createJoinRequestAction = new CreateJoinRequestAction(joinRequestRepository, workspaceRepository, memberRepository, currentUserService, notificationPublisher, activityLogger);
        rejectJoinRequestAction = new RejectJoinRequestAction(joinRequestRepository, currentUserService, iamIntegrationService, activityLogger);
        Instant now = Instant.now();
        currentUserId = UUID.randomUUID();
        IamUser currentUser = new IamUser(currentUserId, Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin User", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void createJoinRequest_byWorkspaceCode_success() {
        UUID workspaceId = UUID.randomUUID();
        CreateWorkspaceJoinRequestCommand command = new CreateWorkspaceJoinRequestCommand(
                null, "DEV_WS", "Please let me in");

        when(workspaceRepository.findByCode(any())).thenReturn(Optional.of(
                workspace(workspaceId, WorkspaceJoinPolicy.REQUEST_TO_JOIN)));
        when(memberRepository.isActiveMember(workspaceId, currentUserId)).thenReturn(false);
        when(joinRequestRepository.findPendingByWorkspaceAndUser(workspaceId, currentUserId))
                .thenReturn(Optional.empty());
        when(joinRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceJoinRequestResponse response = createJoinRequestAction.execute(command);

        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.message()).isEqualTo("Please let me in");
        verify(joinRequestRepository).save(any(WorkspaceJoinRequest.class));
    }

    @Test
    void createJoinRequest_inviteOnly_throws422() {
        UUID workspaceId = UUID.randomUUID();
        CreateWorkspaceJoinRequestCommand command = new CreateWorkspaceJoinRequestCommand(
                workspaceId, null, null);

        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(
                workspace(workspaceId, WorkspaceJoinPolicy.INVITE_ONLY)));

        assertThatThrownBy(() -> createJoinRequestAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUIRES_INVITATION.code());
                });

        verify(joinRequestRepository, never()).save(any());
    }

    @Test
    void createJoinRequest_alreadyPending_throws409() {
        UUID workspaceId = UUID.randomUUID();
        CreateWorkspaceJoinRequestCommand command = new CreateWorkspaceJoinRequestCommand(
                workspaceId, null, null);
        WorkspaceJoinRequest existing = pendingRequest(workspaceId, currentUserId);

        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(
                workspace(workspaceId, WorkspaceJoinPolicy.REQUEST_TO_JOIN)));
        when(memberRepository.isActiveMember(workspaceId, currentUserId)).thenReturn(false);
        when(joinRequestRepository.findPendingByWorkspaceAndUser(workspaceId, currentUserId))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> createJoinRequestAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_ALREADY_PENDING.code());
                });
    }

    @Test
    void createJoinRequest_alreadyMember_throws409() {
        UUID workspaceId = UUID.randomUUID();
        CreateWorkspaceJoinRequestCommand command = new CreateWorkspaceJoinRequestCommand(
                workspaceId, null, null);

        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(
                workspace(workspaceId, WorkspaceJoinPolicy.REQUEST_TO_JOIN)));
        when(memberRepository.isActiveMember(workspaceId, currentUserId)).thenReturn(true);

        assertThatThrownBy(() -> createJoinRequestAction.execute(command))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(ae.getErrorCode()).isEqualTo(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_ALREADY_MEMBER.code());
                });
    }

    @Test
    void approveJoinRequest_addsMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        UUID requestorId = UUID.randomUUID();
        ReviewWorkspaceJoinRequestCommand command = new ReviewWorkspaceJoinRequestCommand(
                requestId, workspaceId, null);
        WorkspaceJoinRequest request = pendingRequest(workspaceId, requestorId, requestId);

        when(joinRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(joinRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceJoinRequestResponse response = approveJoinRequestAction.execute(command);

        assertThat(response.status()).isEqualTo("APPROVED");
        verify(memberRepository).save(any(WorkspaceMember.class));
    }

    @Test
    void rejectJoinRequest_setsRejected() {
        UUID workspaceId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        ReviewWorkspaceJoinRequestCommand command = new ReviewWorkspaceJoinRequestCommand(
                requestId, workspaceId, "Not a good fit");
        WorkspaceJoinRequest request = pendingRequest(workspaceId, UUID.randomUUID(), requestId);

        when(joinRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(joinRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceJoinRequestResponse response = rejectJoinRequestAction.execute(command);

        assertThat(response.status()).isEqualTo("REJECTED");
        assertThat(response.reviewNote()).isEqualTo("Not a good fit");
        verify(memberRepository, never()).save(any());
    }

    @Test
    void cancelJoinRequest_byRequestor_success() {
        UUID requestId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();
        WorkspaceJoinRequest request = pendingRequest(workspaceId, currentUserId, requestId);

        when(joinRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(joinRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceJoinRequestResponse response = cancelJoinRequestAction.execute(new CancelJoinRequestCommand(requestId));

        assertThat(response.status()).isEqualTo("CANCELLED");
    }

    @Test
    void cancelJoinRequest_byOtherUser_throws403() {
        UUID requestId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        WorkspaceJoinRequest request = pendingRequest(workspaceId, otherUserId, requestId);

        when(joinRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> cancelJoinRequestAction.execute(new CancelJoinRequestCommand(requestId)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                    assertThat(ae.getErrorCode()).isEqualTo(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_FORBIDDEN.code());
                });

        verify(joinRequestRepository, never()).save(any());
    }

    private Workspace workspace(UUID id, WorkspaceJoinPolicy joinPolicy) {
        Instant now = Instant.now();
        return new Workspace(id, UUID.randomUUID(), WorkspaceCode.of("DEV_WS"), "Dev Workspace", null,
                UUID.randomUUID(), WorkspaceVisibility.PRIVATE, joinPolicy, WorkspaceStatus.ACTIVE, 0, now, now);
    }

    private WorkspaceJoinRequest pendingRequest(UUID workspaceId, UUID requestedByUserId) {
        Instant now = Instant.now();
        return new WorkspaceJoinRequest(UUID.randomUUID(), workspaceId, requestedByUserId,
                null, WorkspaceJoinRequestStatus.PENDING, null, null, null, now, now);
    }

    private WorkspaceJoinRequest pendingRequest(UUID workspaceId, UUID requestedByUserId, UUID id) {
        Instant now = Instant.now();
        return new WorkspaceJoinRequest(id, workspaceId, requestedByUserId,
                null, WorkspaceJoinRequestStatus.PENDING, null, null, null, now, now);
    }
}

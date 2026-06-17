package com.company.scopery.modules.workspace.onboarding.application;

import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import com.company.scopery.modules.workspace.context.domain.WorkspaceUserContextRepository;
import com.company.scopery.modules.workspace.invitation.application.WorkspaceInvitationApplicationService;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
import com.company.scopery.modules.workspace.joinrequest.application.WorkspaceJoinRequestApplicationService;
import com.company.scopery.modules.workspace.joinrequest.application.response.WorkspaceJoinRequestResponse;
import com.company.scopery.modules.workspace.onboarding.application.response.WorkspaceOnboardingStatusResponse;
import com.company.scopery.modules.workspace.onboarding.domain.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.WorkspaceOnboardingStateRepository;
import com.company.scopery.modules.workspace.onboarding.domain.WorkspaceOnboardingStatus;
import com.company.scopery.modules.workspace.onboarding.domain.WorkspaceOnboardingStep;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.workspace.application.WorkspaceApplicationService;
import com.company.scopery.modules.workspace.workspace.application.command.CreateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceOnboardingApplicationServiceTest {

    @Mock private WorkspaceOnboardingStateRepository onboardingRepository;
    @Mock private WorkspaceUserContextRepository contextRepository;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceApplicationService workspaceService;
    @Mock private WorkspaceInvitationApplicationService invitationService;
    @Mock private WorkspaceJoinRequestApplicationService joinRequestService;
    @Mock private WorkspaceActivityLogger activityLogger;

    private WorkspaceOnboardingApplicationService service;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        service = new WorkspaceOnboardingApplicationService(
                onboardingRepository, contextRepository, currentUserService,
                workspaceService, invitationService, joinRequestService, activityLogger);
        Instant now = Instant.now();
        currentUserId = UUID.randomUUID();
        IamUser currentUser = new IamUser(currentUserId, Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin User", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void getStatus_noState_returnsInProgress_chooseOption() {
        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.empty());

        WorkspaceOnboardingStatusResponse response = service.getStatus();

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.IN_PROGRESS.name());
        assertThat(response.currentStep()).isEqualTo(WorkspaceOnboardingStep.CHOOSE_WORKSPACE_OPTION.name());
        assertThat(response.userId()).isEqualTo(currentUserId);
    }

    @Test
    void getStatus_existingCompletedState_returnsCompleted() {
        WorkspaceOnboardingState completedState = WorkspaceOnboardingState.create(currentUserId)
                .onWorkspaceCreated(UUID.randomUUID(), UUID.randomUUID());
        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.of(completedState));

        WorkspaceOnboardingStatusResponse response = service.getStatus();

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.COMPLETED.name());
        assertThat(response.currentStep()).isEqualTo(WorkspaceOnboardingStep.COMPLETED.name());
    }

    @Test
    void createWorkspace_completesOnboarding() {
        UUID orgId = UUID.randomUUID();
        UUID wsId = UUID.randomUUID();
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(currentUserId);
        CreateWorkspaceCommand command = new CreateWorkspaceCommand(orgId, "My Workspace", "MY_WS", null, null, null);
        WorkspaceDetailResponse wsResponse = new WorkspaceDetailResponse(
                wsId, orgId, "MY_WS", "My Workspace", null, currentUserId,
                "PRIVATE", "INVITE_ONLY", "ACTIVE", Instant.now(), Instant.now(), true);

        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.of(state));
        when(workspaceService.createWorkspace(command)).thenReturn(wsResponse);
        when(onboardingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(contextRepository.findByUserId(currentUserId)).thenReturn(Optional.empty());
        when(contextRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceOnboardingStatusResponse response = service.createWorkspace(command);

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.COMPLETED.name());
        assertThat(response.createdWorkspaceId()).isEqualTo(wsId);
        verify(onboardingRepository).save(any(WorkspaceOnboardingState.class));
    }

    @Test
    void acceptInvitation_completesOnboarding() {
        UUID workspaceId = UUID.randomUUID();
        String rawCode = "ONBOARDINGTESTCODE01";
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(currentUserId);
        WorkspaceInvitationResponse invResponse = new WorkspaceInvitationResponse(
                UUID.randomUUID(), workspaceId, null, null, "ONBO",
                "PENDING", null, 1, null, Instant.now());

        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.of(state));
        when(invitationService.acceptInvitation(rawCode)).thenReturn(invResponse);
        when(onboardingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(contextRepository.findByUserId(currentUserId)).thenReturn(Optional.empty());
        when(contextRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceOnboardingStatusResponse response = service.acceptInvitation(rawCode);

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.COMPLETED.name());
        assertThat(response.targetWorkspaceId()).isEqualTo(workspaceId);
    }

    @Test
    void joinRequest_setsWaitingForApproval() {
        UUID workspaceId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(currentUserId);
        WorkspaceJoinRequestResponse jrResponse = new WorkspaceJoinRequestResponse(
                requestId, workspaceId, currentUserId, null, "PENDING",
                null, null, null, Instant.now(), Instant.now());

        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.of(state));
        when(joinRequestService.createJoinRequest(any())).thenReturn(jrResponse);
        when(onboardingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceOnboardingStatusResponse response = service.joinRequest(workspaceId, null, "Please add me");

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.WAITING_FOR_APPROVAL.name());
        assertThat(response.joinRequestId()).isEqualTo(requestId);
    }

    @Test
    void cancel_setsCancelled() {
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(currentUserId);

        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.of(state));
        when(onboardingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceOnboardingStatusResponse response = service.cancel();

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.CANCELLED.name());
        assertThat(response.currentStep()).isEqualTo(WorkspaceOnboardingStep.CANCELLED.name());
    }
}

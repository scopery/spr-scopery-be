package com.company.scopery.modules.workspace.onboarding.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContext;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContextRepository;
import com.company.scopery.modules.workspace.invitation.application.action.AcceptInvitationAction;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
import com.company.scopery.modules.workspace.joinrequest.application.action.CancelJoinRequestAction;
import com.company.scopery.modules.workspace.onboarding.application.action.AcceptInvitationOnboardingAction;
import com.company.scopery.modules.workspace.onboarding.application.action.ChooseOnboardingOptionAction;
import com.company.scopery.modules.workspace.onboarding.application.action.CreateWorkspaceOnboardingAction;
import com.company.scopery.modules.workspace.onboarding.application.action.ResetOnboardingChoiceAction;
import com.company.scopery.modules.workspace.onboarding.application.command.AcceptInvitationOnboardingCommand;
import com.company.scopery.modules.workspace.onboarding.application.command.ChooseOnboardingOptionCommand;
import com.company.scopery.modules.workspace.onboarding.application.command.CreateWorkspaceOnboardingCommand;
import com.company.scopery.modules.workspace.onboarding.application.response.WorkspaceOnboardingStatusResponse;
import com.company.scopery.modules.workspace.onboarding.application.service.OnboardingQueryService;
import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingOption;
import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingStatus;
import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingStep;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingStateRepository;
import com.company.scopery.modules.workspace.organization.application.action.CreateOrganizationAction;
import com.company.scopery.modules.workspace.organization.application.response.OrganizationResponse;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.workspace.application.action.CreateWorkspaceAction;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceOnboardingActionTest {

    @Mock private WorkspaceOnboardingStateRepository onboardingRepository;
    @Mock private WorkspaceUserContextRepository contextRepository;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private CreateOrganizationAction createOrganizationAction;
    @Mock private CreateWorkspaceAction createWorkspaceAction;
    @Mock private AcceptInvitationAction acceptInvitationAction;
    @Mock private CancelJoinRequestAction cancelJoinRequestAction;
    @Mock private WorkspaceActivityLogger activityLogger;

    private OnboardingQueryService onboardingQueryService;
    private CreateWorkspaceOnboardingAction createWorkspaceOnboardingAction;
    private AcceptInvitationOnboardingAction acceptInvitationOnboardingAction;
    private ChooseOnboardingOptionAction chooseOnboardingOptionAction;
    private ResetOnboardingChoiceAction resetOnboardingChoiceAction;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        onboardingQueryService = new OnboardingQueryService(onboardingRepository, currentUserService);
        createWorkspaceOnboardingAction = new CreateWorkspaceOnboardingAction(
                onboardingRepository, contextRepository, currentUserService,
                createOrganizationAction, createWorkspaceAction, activityLogger);
        acceptInvitationOnboardingAction = new AcceptInvitationOnboardingAction(
                onboardingRepository, contextRepository, currentUserService,
                acceptInvitationAction, activityLogger);
        chooseOnboardingOptionAction = new ChooseOnboardingOptionAction(onboardingRepository, currentUserService);
        resetOnboardingChoiceAction = new ResetOnboardingChoiceAction(
                onboardingRepository, currentUserService, cancelJoinRequestAction, activityLogger);

        Instant now = Instant.now();
        currentUserId = UUID.randomUUID();
        IamUser currentUser = IamUser.of(currentUserId, Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin User", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void getStatus_noState_returnsInProgress_chooseOption() {
        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.empty());

        WorkspaceOnboardingStatusResponse response = onboardingQueryService.getStatus();

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.IN_PROGRESS.name());
        assertThat(response.currentStep()).isEqualTo(WorkspaceOnboardingStep.CHOOSE_WORKSPACE_OPTION.name());
        assertThat(response.userId()).isEqualTo(currentUserId);
    }

    @Test
    void getStatus_existingCompletedState_returnsCompleted() {
        WorkspaceOnboardingState completedState = WorkspaceOnboardingState.create(currentUserId)
                .onWorkspaceCreated(UUID.randomUUID(), UUID.randomUUID());
        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.of(completedState));

        WorkspaceOnboardingStatusResponse response = onboardingQueryService.getStatus();

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.COMPLETED.name());
        assertThat(response.currentStep()).isEqualTo(WorkspaceOnboardingStep.COMPLETED.name());
    }

    @Test
    void createWorkspace_completesOnboarding() {
        UUID orgId = UUID.randomUUID();
        UUID wsId = UUID.randomUUID();
        Instant now = Instant.now();
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(currentUserId);
        OrganizationResponse orgResponse = new OrganizationResponse(
                orgId, "MY_ORG", "My Org", null, currentUserId, "ACTIVE", 0, now, now);
        WorkspaceDetailResponse wsResponse = new WorkspaceDetailResponse(
                wsId, orgId, "MY_WS", "My Workspace", null, currentUserId,
                "PRIVATE", "INVITE_ONLY", "ACTIVE", now, now, true);

        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.of(state));
        when(createOrganizationAction.execute(any())).thenReturn(orgResponse);
        when(createWorkspaceAction.execute(any())).thenReturn(wsResponse);
        when(onboardingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(contextRepository.findByUserId(currentUserId)).thenReturn(Optional.empty());
        when(contextRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceOnboardingStatusResponse response = createWorkspaceOnboardingAction.execute(
                new CreateWorkspaceOnboardingCommand("My Org", "MY_ORG", "My Workspace", "MY_WS", null));

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.COMPLETED.name());
        assertThat(response.createdWorkspaceId()).isEqualTo(wsId);
        verify(createOrganizationAction).execute(any());
        verify(createWorkspaceAction).execute(any());
        verify(onboardingRepository).save(any(WorkspaceOnboardingState.class));
        var contextCaptor = org.mockito.ArgumentCaptor.forClass(WorkspaceUserContext.class);
        verify(contextRepository).save(contextCaptor.capture());
        assertThat(contextCaptor.getValue().currentWorkspaceId()).isEqualTo(wsId);
        assertThat(contextCaptor.getValue().onboardingCompletedAt()).isNotNull();
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
        when(acceptInvitationAction.execute(any())).thenReturn(invResponse);
        when(onboardingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(contextRepository.findByUserId(currentUserId)).thenReturn(Optional.empty());
        when(contextRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceOnboardingStatusResponse response = acceptInvitationOnboardingAction.execute(
                new AcceptInvitationOnboardingCommand(rawCode));

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.COMPLETED.name());
        assertThat(response.targetWorkspaceId()).isEqualTo(workspaceId);
        var contextCaptor = org.mockito.ArgumentCaptor.forClass(WorkspaceUserContext.class);
        verify(contextRepository).save(contextCaptor.capture());
        assertThat(contextCaptor.getValue().currentWorkspaceId()).isEqualTo(workspaceId);
        assertThat(contextCaptor.getValue().onboardingCompletedAt()).isNotNull();
    }

    @Test
    void chooseOption_requestToJoin_throwsNotSupported() {
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(currentUserId);

        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.of(state));

        assertThatThrownBy(() -> chooseOnboardingOptionAction.execute(
                new ChooseOnboardingOptionCommand(WorkspaceOnboardingOption.REQUEST_TO_JOIN)))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("not supported");
    }

    @Test
    void resetChoice_fromWaitingApproval_returnsChooseOption() {
        UUID joinRequestId = UUID.randomUUID();
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(currentUserId)
                .onJoinRequestSubmitted(joinRequestId);

        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.of(state));
        when(onboardingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceOnboardingStatusResponse response = resetOnboardingChoiceAction.execute();

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.IN_PROGRESS.name());
        assertThat(response.currentStep()).isEqualTo(WorkspaceOnboardingStep.CHOOSE_WORKSPACE_OPTION.name());
        verify(cancelJoinRequestAction).execute(any());
    }

    @Test
    void resetChoice_fromCreateWorkspace_returnsChooseOption() {
        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(currentUserId)
                .chooseOption(WorkspaceOnboardingOption.CREATE_WORKSPACE);

        when(onboardingRepository.findByUserId(currentUserId)).thenReturn(Optional.of(state));
        when(onboardingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WorkspaceOnboardingStatusResponse response = resetOnboardingChoiceAction.execute();

        assertThat(response.status()).isEqualTo(WorkspaceOnboardingStatus.IN_PROGRESS.name());
        assertThat(response.currentStep()).isEqualTo(WorkspaceOnboardingStep.CHOOSE_WORKSPACE_OPTION.name());
        assertThat(response.selectedOption()).isNull();
        verify(activityLogger).logSuccess(
                eq(WorkspaceEntityTypes.WORKSPACE_ONBOARDING),
                eq(state.id()),
                eq(WorkspaceActivityActions.RESET_ONBOARDING_CHOICE),
                eq("Onboarding choice reset to option selection"));
    }
}

package com.company.scopery.modules.workspace.onboarding.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.context.domain.WorkspaceUserContext;
import com.company.scopery.modules.workspace.context.domain.WorkspaceUserContextRepository;
import com.company.scopery.modules.workspace.invitation.application.WorkspaceInvitationApplicationService;
import com.company.scopery.modules.workspace.joinrequest.application.WorkspaceJoinRequestApplicationService;
import com.company.scopery.modules.workspace.joinrequest.application.command.CreateWorkspaceJoinRequestCommand;
import com.company.scopery.modules.workspace.onboarding.application.response.WorkspaceOnboardingStatusResponse;
import com.company.scopery.modules.workspace.onboarding.domain.WorkspaceOnboardingOption;
import com.company.scopery.modules.workspace.onboarding.domain.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.WorkspaceOnboardingStateRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.workspace.application.WorkspaceApplicationService;
import com.company.scopery.modules.workspace.workspace.application.command.CreateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceDetailResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WorkspaceOnboardingApplicationService {

    private final WorkspaceOnboardingStateRepository onboardingRepository;
    private final WorkspaceUserContextRepository contextRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceApplicationService workspaceService;
    private final WorkspaceInvitationApplicationService invitationService;
    private final WorkspaceJoinRequestApplicationService joinRequestService;
    private final WorkspaceActivityLogger activityLogger;

    public WorkspaceOnboardingApplicationService(
            WorkspaceOnboardingStateRepository onboardingRepository,
            WorkspaceUserContextRepository contextRepository,
            CurrentUserAuthorizationService currentUserService,
            WorkspaceApplicationService workspaceService,
            WorkspaceInvitationApplicationService invitationService,
            WorkspaceJoinRequestApplicationService joinRequestService,
            WorkspaceActivityLogger activityLogger) {
        this.onboardingRepository = onboardingRepository;
        this.contextRepository = contextRepository;
        this.currentUserService = currentUserService;
        this.workspaceService = workspaceService;
        this.invitationService = invitationService;
        this.joinRequestService = joinRequestService;
        this.activityLogger = activityLogger;
    }

    @Transactional(readOnly = true)
    public WorkspaceOnboardingStatusResponse getStatus() {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceOnboardingState state = onboardingRepository.findByUserId(userId)
                .orElseGet(() -> WorkspaceOnboardingState.create(userId));
        return WorkspaceOnboardingStatusResponse.from(state);
    }

    @Transactional
    public WorkspaceOnboardingStatusResponse start() {
        UUID userId = currentUserService.resolveCurrentUser().id();

        if (onboardingRepository.findByUserId(userId).isPresent()) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_ONBOARDING_ALREADY_COMPLETED,
                    "Your workspace onboarding is already in progress or completed", null);
        }

        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(userId);
        WorkspaceOnboardingState saved = onboardingRepository.save(state);
        return WorkspaceOnboardingStatusResponse.from(saved);
    }

    @Transactional
    public WorkspaceOnboardingStatusResponse chooseOption(WorkspaceOnboardingOption option) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceOnboardingState state = loadOrCreate(userId);
        WorkspaceOnboardingState updated = state.chooseOption(option);
        return WorkspaceOnboardingStatusResponse.from(onboardingRepository.save(updated));
    }

    @Transactional
    public WorkspaceOnboardingStatusResponse createWorkspace(CreateWorkspaceCommand command) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceOnboardingState state = loadOrCreate(userId);

        WorkspaceDetailResponse ws = workspaceService.createWorkspace(command);

        WorkspaceOnboardingState updated = state.onWorkspaceCreated(ws.organizationId(), ws.id());
        WorkspaceOnboardingState saved = onboardingRepository.save(updated);

        setCurrentWorkspace(userId, ws.id());

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_ONBOARDING, saved.id(),
                WorkspaceActivityActions.COMPLETE_ONBOARDING, "Onboarding completed via workspace creation");

        return WorkspaceOnboardingStatusResponse.from(saved);
    }

    @Transactional
    public WorkspaceOnboardingStatusResponse acceptInvitation(String rawCode) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceOnboardingState state = loadOrCreate(userId);

        var invResponse = invitationService.acceptInvitation(rawCode);

        WorkspaceOnboardingState updated = state.onInvitationAccepted(invResponse.workspaceId());
        WorkspaceOnboardingState saved = onboardingRepository.save(updated);

        setCurrentWorkspace(userId, invResponse.workspaceId());

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_ONBOARDING, saved.id(),
                WorkspaceActivityActions.COMPLETE_ONBOARDING, "Onboarding completed via invitation acceptance");

        return WorkspaceOnboardingStatusResponse.from(saved);
    }

    @Transactional
    public WorkspaceOnboardingStatusResponse joinRequest(UUID workspaceId, String workspaceCode, String message) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceOnboardingState state = loadOrCreate(userId);

        var requestResponse = joinRequestService.createJoinRequest(
                new CreateWorkspaceJoinRequestCommand(workspaceId, workspaceCode, message));

        WorkspaceOnboardingState updated = state.onJoinRequestSubmitted(requestResponse.id());
        WorkspaceOnboardingState saved = onboardingRepository.save(updated);

        return WorkspaceOnboardingStatusResponse.from(saved);
    }

    @Transactional
    public WorkspaceOnboardingStatusResponse cancel() {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceOnboardingState state = loadOrCreate(userId);
        WorkspaceOnboardingState cancelled = state.cancel();
        return WorkspaceOnboardingStatusResponse.from(onboardingRepository.save(cancelled));
    }

    private WorkspaceOnboardingState loadOrCreate(UUID userId) {
        return onboardingRepository.findByUserId(userId)
                .orElseGet(() -> onboardingRepository.save(WorkspaceOnboardingState.create(userId)));
    }

    private void setCurrentWorkspace(UUID userId, UUID workspaceId) {
        WorkspaceUserContext ctx = contextRepository.findByUserId(userId)
                .orElseGet(() -> WorkspaceUserContext.create(userId));
        contextRepository.save(ctx.switchTo(workspaceId));
    }
}

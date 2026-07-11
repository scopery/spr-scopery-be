package com.company.scopery.modules.workspace.onboarding.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContext;
import com.company.scopery.modules.workspace.context.domain.model.WorkspaceUserContextRepository;
import com.company.scopery.modules.workspace.invitation.application.action.AcceptInvitationAction;
import com.company.scopery.modules.workspace.invitation.application.command.AcceptInvitationCommand;
import com.company.scopery.modules.workspace.onboarding.application.command.AcceptInvitationOnboardingCommand;
import com.company.scopery.modules.workspace.onboarding.application.response.WorkspaceOnboardingStatusResponse;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingStateRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class AcceptInvitationOnboardingAction {

    private final WorkspaceOnboardingStateRepository onboardingRepository;
    private final WorkspaceUserContextRepository contextRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final AcceptInvitationAction acceptInvitationAction;
    private final WorkspaceActivityLogger activityLogger;

    public AcceptInvitationOnboardingAction(WorkspaceOnboardingStateRepository onboardingRepository,
                                             WorkspaceUserContextRepository contextRepository,
                                             CurrentUserAuthorizationService currentUserService,
                                             AcceptInvitationAction acceptInvitationAction,
                                             WorkspaceActivityLogger activityLogger) {
        this.onboardingRepository = onboardingRepository;
        this.contextRepository = contextRepository;
        this.currentUserService = currentUserService;
        this.acceptInvitationAction = acceptInvitationAction;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceOnboardingStatusResponse execute(AcceptInvitationOnboardingCommand command) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceOnboardingState state = loadOrCreate(userId);

        var invResponse = acceptInvitationAction.execute(new AcceptInvitationCommand(command.rawCode()));

        WorkspaceOnboardingState updated = state.onInvitationAccepted(invResponse.workspaceId());
        WorkspaceOnboardingState saved = onboardingRepository.save(updated);

        setCurrentWorkspace(userId, invResponse.workspaceId());

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_ONBOARDING, saved.id(),
                WorkspaceActivityActions.COMPLETE_ONBOARDING, "Onboarding completed via invitation acceptance");

        return WorkspaceOnboardingStatusResponse.from(saved);
    }

    private WorkspaceOnboardingState loadOrCreate(UUID userId) {
        return onboardingRepository.findByUserId(userId)
                .orElseGet(() -> onboardingRepository.save(WorkspaceOnboardingState.create(userId)));
    }

    private void setCurrentWorkspace(UUID userId, UUID workspaceId) {
        WorkspaceUserContext ctx = contextRepository.findByUserId(userId)
                .orElseGet(() -> WorkspaceUserContext.create(userId));
        contextRepository.save(ctx.switchTo(workspaceId).completeOnboarding());
    }
}

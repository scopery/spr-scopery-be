package com.company.scopery.modules.workspace.onboarding.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.joinrequest.application.action.CancelJoinRequestAction;
import com.company.scopery.modules.workspace.joinrequest.application.command.CancelJoinRequestCommand;
import com.company.scopery.modules.workspace.onboarding.application.response.WorkspaceOnboardingStatusResponse;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingStateRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ResetOnboardingChoiceAction {

    private final WorkspaceOnboardingStateRepository onboardingRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final CancelJoinRequestAction cancelJoinRequestAction;
    private final WorkspaceActivityLogger activityLogger;

    public ResetOnboardingChoiceAction(WorkspaceOnboardingStateRepository onboardingRepository,
                                        CurrentUserAuthorizationService currentUserService,
                                        CancelJoinRequestAction cancelJoinRequestAction,
                                        WorkspaceActivityLogger activityLogger) {
        this.onboardingRepository = onboardingRepository;
        this.currentUserService = currentUserService;
        this.cancelJoinRequestAction = cancelJoinRequestAction;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceOnboardingStatusResponse execute() {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceOnboardingState state = loadOrCreate(userId);
        cancelLegacyJoinRequestIfPresent(state);
        WorkspaceOnboardingState reset = state.resetChoice();
        WorkspaceOnboardingState saved = onboardingRepository.save(reset);

        activityLogger.logSuccess(
                WorkspaceEntityTypes.WORKSPACE_ONBOARDING,
                saved.id(),
                WorkspaceActivityActions.RESET_ONBOARDING_CHOICE,
                "Onboarding choice reset to option selection");

        return WorkspaceOnboardingStatusResponse.from(saved);
    }

    private WorkspaceOnboardingState loadOrCreate(UUID userId) {
        return onboardingRepository.findByUserId(userId)
                .orElseGet(() -> onboardingRepository.save(WorkspaceOnboardingState.create(userId)));
    }

    private void cancelLegacyJoinRequestIfPresent(WorkspaceOnboardingState state) {
        if (state.joinRequestId() == null) {
            return;
        }
        try {
            cancelJoinRequestAction.execute(new CancelJoinRequestCommand(state.joinRequestId()));
        } catch (AppException ex) {
            if (!WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_NOT_PENDING.code().equals(ex.getErrorCode())) {
                throw ex;
            }
        }
    }
}

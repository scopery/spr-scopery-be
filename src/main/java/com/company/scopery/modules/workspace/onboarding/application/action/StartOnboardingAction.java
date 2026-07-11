package com.company.scopery.modules.workspace.onboarding.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.onboarding.application.response.WorkspaceOnboardingStatusResponse;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingStateRepository;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class StartOnboardingAction {

    private final WorkspaceOnboardingStateRepository onboardingRepository;
    private final CurrentUserAuthorizationService currentUserService;

    public StartOnboardingAction(WorkspaceOnboardingStateRepository onboardingRepository,
                                  CurrentUserAuthorizationService currentUserService) {
        this.onboardingRepository = onboardingRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public WorkspaceOnboardingStatusResponse execute() {
        UUID userId = currentUserService.resolveCurrentUser().id();

        if (onboardingRepository.findByUserId(userId).isPresent()) {
            throw WorkspaceExceptions.onboardingAlreadyInProgressOrCompleted();
        }

        WorkspaceOnboardingState state = WorkspaceOnboardingState.create(userId);
        WorkspaceOnboardingState saved = onboardingRepository.save(state);
        return WorkspaceOnboardingStatusResponse.from(saved);
    }
}

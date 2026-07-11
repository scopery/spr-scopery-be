package com.company.scopery.modules.workspace.onboarding.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.onboarding.application.response.WorkspaceOnboardingStatusResponse;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingStateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OnboardingQueryService {

    private final WorkspaceOnboardingStateRepository onboardingRepository;
    private final CurrentUserAuthorizationService currentUserService;

    public OnboardingQueryService(WorkspaceOnboardingStateRepository onboardingRepository,
                                   CurrentUserAuthorizationService currentUserService) {
        this.onboardingRepository = onboardingRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public WorkspaceOnboardingStatusResponse getStatus() {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceOnboardingState state = onboardingRepository.findByUserId(userId)
                .orElseGet(() -> WorkspaceOnboardingState.create(userId));
        return WorkspaceOnboardingStatusResponse.from(state);
    }
}

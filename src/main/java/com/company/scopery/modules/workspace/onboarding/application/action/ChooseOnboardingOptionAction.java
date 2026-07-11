package com.company.scopery.modules.workspace.onboarding.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.onboarding.application.command.ChooseOnboardingOptionCommand;
import com.company.scopery.modules.workspace.onboarding.application.response.WorkspaceOnboardingStatusResponse;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingStateRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ChooseOnboardingOptionAction {

    private final WorkspaceOnboardingStateRepository onboardingRepository;
    private final CurrentUserAuthorizationService currentUserService;

    public ChooseOnboardingOptionAction(WorkspaceOnboardingStateRepository onboardingRepository,
                                         CurrentUserAuthorizationService currentUserService) {
        this.onboardingRepository = onboardingRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public WorkspaceOnboardingStatusResponse execute(ChooseOnboardingOptionCommand command) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        WorkspaceOnboardingState state = loadOrCreate(userId);
        WorkspaceOnboardingState updated = state.chooseOption(command.option());
        return WorkspaceOnboardingStatusResponse.from(onboardingRepository.save(updated));
    }

    private WorkspaceOnboardingState loadOrCreate(UUID userId) {
        return onboardingRepository.findByUserId(userId)
                .orElseGet(() -> onboardingRepository.save(WorkspaceOnboardingState.create(userId)));
    }
}

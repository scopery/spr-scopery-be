package com.company.scopery.modules.workspace.onboarding.api.request;

import com.company.scopery.modules.workspace.onboarding.domain.WorkspaceOnboardingOption;
import jakarta.validation.constraints.NotNull;

public record ChooseOnboardingOptionRequest(@NotNull WorkspaceOnboardingOption option) {}

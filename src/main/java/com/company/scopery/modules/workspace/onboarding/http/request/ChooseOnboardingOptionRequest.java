package com.company.scopery.modules.workspace.onboarding.http.request;

import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingOption;
import jakarta.validation.constraints.NotNull;

public record ChooseOnboardingOptionRequest(@NotNull WorkspaceOnboardingOption option) {}

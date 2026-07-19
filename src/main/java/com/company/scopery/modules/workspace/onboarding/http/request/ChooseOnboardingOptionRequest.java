package com.company.scopery.modules.workspace.onboarding.http.request;

import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request payload for selecting an onboarding path option")
public record ChooseOnboardingOptionRequest(
        @Schema(description = "The onboarding option the user selects to proceed with", example = "CREATE_WORKSPACE")
        @NotNull WorkspaceOnboardingOption option) {}

package com.company.scopery.modules.workspace.onboarding.http.request;

import jakarta.validation.constraints.NotBlank;

public record OnboardingCreateWorkspaceRequest(
        @NotBlank String organizationName,
        @NotBlank String organizationCode,
        @NotBlank String workspaceName,
        @NotBlank String workspaceCode,
        String workspaceDescription) {
}

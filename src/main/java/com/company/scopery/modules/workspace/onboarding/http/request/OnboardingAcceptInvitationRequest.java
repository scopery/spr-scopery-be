package com.company.scopery.modules.workspace.onboarding.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for accepting an organization or workspace invitation during onboarding")
public record OnboardingAcceptInvitationRequest(
        @Schema(description = "The invitation code received via email", example = "abc123xyz")
        @NotBlank String code) {}

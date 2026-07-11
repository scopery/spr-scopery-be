package com.company.scopery.modules.workspace.onboarding.http.request;

import jakarta.validation.constraints.NotBlank;

public record OnboardingAcceptInvitationRequest(@NotBlank String code) {}

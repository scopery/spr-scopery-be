package com.company.scopery.modules.workspace.onboarding.api.request;

import java.util.UUID;

public record OnboardingJoinRequestRequest(
        UUID workspaceId,
        String workspaceCode,
        String message) {}

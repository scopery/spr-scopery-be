package com.company.scopery.modules.workspace.onboarding.domain.enums;

public enum WorkspaceOnboardingOption {
    CREATE_WORKSPACE,
    JOIN_WITH_INVITATION,
    /** @deprecated Legacy DB value — no longer selectable during onboarding. */
    @Deprecated
    REQUEST_TO_JOIN;

    public boolean isSupportedDuringOnboarding() {
        return this == CREATE_WORKSPACE || this == JOIN_WITH_INVITATION;
    }
}

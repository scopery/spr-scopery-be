package com.company.scopery.modules.workspace.onboarding.domain.enums;

public enum WorkspaceOnboardingStep {
    CHOOSE_WORKSPACE_OPTION,
    CREATE_WORKSPACE,
    ENTER_INVITATION_CODE,
    ACCEPT_INVITATION,
    REQUEST_JOIN_WORKSPACE,
    WAITING_JOIN_APPROVAL,
    COMPLETED,
    CANCELLED,
    FAILED;

    /** Steps where the user may return to {@link #CHOOSE_WORKSPACE_OPTION}. */
    public boolean isChoiceResettable() {
        return this == CREATE_WORKSPACE
                || this == ENTER_INVITATION_CODE
                || this == REQUEST_JOIN_WORKSPACE;
    }
}

package com.company.scopery.modules.workspace.onboarding.application.response;

import com.company.scopery.modules.workspace.onboarding.domain.WorkspaceOnboardingState;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceOnboardingStatusResponse(
        UUID userId,
        String status,
        String currentStep,
        String selectedOption,
        UUID targetWorkspaceId,
        UUID createdOrganizationId,
        UUID createdWorkspaceId,
        UUID invitationId,
        UUID joinRequestId,
        String failureReason,
        Instant completedAt,
        Instant lastSeenAt) {

    public static WorkspaceOnboardingStatusResponse from(WorkspaceOnboardingState s) {
        return new WorkspaceOnboardingStatusResponse(
                s.userId(), s.status().name(), s.currentStep().name(),
                s.selectedOption() != null ? s.selectedOption().name() : null,
                s.targetWorkspaceId(), s.createdOrganizationId(), s.createdWorkspaceId(),
                s.invitationId(), s.joinRequestId(), s.failureReason(),
                s.completedAt(), s.lastSeenAt());
    }
}

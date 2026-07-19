package com.company.scopery.modules.workspace.onboarding.application.response;

import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Current onboarding status and progress for a user")
public record WorkspaceOnboardingStatusResponse(
        @Schema(description = "ID of the user undergoing onboarding", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,

        @Schema(description = "Overall onboarding status", example = "IN_PROGRESS")
        String status,

        @Schema(description = "Current onboarding step the user is on", example = "CHOOSE_OPTION")
        String currentStep,

        @Schema(description = "Onboarding option the user selected", example = "CREATE_WORKSPACE", nullable = true)
        String selectedOption,

        @Schema(description = "ID of the target workspace the user is joining or creating", example = "550e8400-e29b-41d4-a716-446655440001", nullable = true)
        UUID targetWorkspaceId,

        @Schema(description = "ID of the organization created during onboarding", example = "550e8400-e29b-41d4-a716-446655440002", nullable = true)
        UUID createdOrganizationId,

        @Schema(description = "ID of the workspace created during onboarding", example = "550e8400-e29b-41d4-a716-446655440003", nullable = true)
        UUID createdWorkspaceId,

        @Schema(description = "ID of the invitation the user is accepting during onboarding", example = "550e8400-e29b-41d4-a716-446655440004", nullable = true)
        UUID invitationId,

        @Schema(description = "ID of the join request submitted during onboarding", example = "550e8400-e29b-41d4-a716-446655440005", nullable = true)
        UUID joinRequestId,

        @Schema(description = "Reason for onboarding failure if applicable", example = "Invitation token expired", nullable = true)
        String failureReason,

        @Schema(description = "Timestamp when onboarding was completed", example = "2026-07-17T03:00:00.000000Z", nullable = true)
        Instant completedAt,

        @Schema(description = "Timestamp when the user last interacted with the onboarding flow", example = "2026-07-17T03:00:00.000000Z", nullable = true)
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

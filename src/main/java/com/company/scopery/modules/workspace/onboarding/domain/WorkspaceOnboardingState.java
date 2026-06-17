package com.company.scopery.modules.workspace.onboarding.domain;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;

import java.time.Instant;
import java.util.UUID;

public record WorkspaceOnboardingState(
        UUID id,
        UUID userId,
        WorkspaceOnboardingStatus status,
        WorkspaceOnboardingStep currentStep,
        WorkspaceOnboardingOption selectedOption,
        UUID targetWorkspaceId,
        UUID createdOrganizationId,
        UUID createdWorkspaceId,
        UUID invitationId,
        UUID joinRequestId,
        String failureReason,
        Instant completedAt,
        Instant cancelledAt,
        Instant lastSeenAt,
        Instant createdAt,
        Instant updatedAt) {

    public static WorkspaceOnboardingState create(UUID userId) {
        Instant now = Instant.now();
        return new WorkspaceOnboardingState(UUID.randomUUID(), userId,
                WorkspaceOnboardingStatus.IN_PROGRESS, WorkspaceOnboardingStep.CHOOSE_WORKSPACE_OPTION,
                null, null, null, null, null, null, null, null, null, now, now, now);
    }

    public WorkspaceOnboardingState chooseOption(WorkspaceOnboardingOption option) {
        assertInProgress();
        WorkspaceOnboardingStep nextStep = switch (option) {
            case CREATE_WORKSPACE -> WorkspaceOnboardingStep.CREATE_WORKSPACE;
            case JOIN_WITH_INVITATION -> WorkspaceOnboardingStep.ENTER_INVITATION_CODE;
            case REQUEST_TO_JOIN -> WorkspaceOnboardingStep.REQUEST_JOIN_WORKSPACE;
        };
        return new WorkspaceOnboardingState(id, userId, status, nextStep, option,
                targetWorkspaceId, createdOrganizationId, createdWorkspaceId,
                invitationId, joinRequestId, failureReason, completedAt, cancelledAt,
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState onWorkspaceCreated(UUID orgId, UUID workspaceId) {
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.COMPLETED, WorkspaceOnboardingStep.COMPLETED,
                selectedOption, workspaceId, orgId, workspaceId,
                invitationId, joinRequestId, null, Instant.now(), null,
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState onInvitationAccepted(UUID workspaceId) {
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.COMPLETED, WorkspaceOnboardingStep.COMPLETED,
                selectedOption, workspaceId, createdOrganizationId, createdWorkspaceId,
                invitationId, joinRequestId, null, Instant.now(), null,
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState onJoinRequestSubmitted(UUID requestId) {
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.WAITING_FOR_APPROVAL, WorkspaceOnboardingStep.WAITING_JOIN_APPROVAL,
                selectedOption, targetWorkspaceId, createdOrganizationId, createdWorkspaceId,
                invitationId, requestId, failureReason, completedAt, cancelledAt,
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState onJoinRequestApproved() {
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.COMPLETED, WorkspaceOnboardingStep.COMPLETED,
                selectedOption, targetWorkspaceId, createdOrganizationId, createdWorkspaceId,
                invitationId, joinRequestId, null, Instant.now(), null,
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState onJoinRequestRejected(String reason) {
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.FAILED, WorkspaceOnboardingStep.FAILED,
                selectedOption, targetWorkspaceId, createdOrganizationId, createdWorkspaceId,
                invitationId, joinRequestId, reason, completedAt, cancelledAt,
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState cancel() {
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.CANCELLED, WorkspaceOnboardingStep.CANCELLED,
                selectedOption, targetWorkspaceId, createdOrganizationId, createdWorkspaceId,
                invitationId, joinRequestId, failureReason, completedAt, Instant.now(),
                Instant.now(), createdAt, Instant.now());
    }

    private void assertInProgress() {
        if (status != WorkspaceOnboardingStatus.IN_PROGRESS) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_ONBOARDING_INVALID_STEP,
                    "Cannot perform this action at the current onboarding step", null);
        }
    }
}

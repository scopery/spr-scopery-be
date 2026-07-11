package com.company.scopery.modules.workspace.onboarding.domain.model;

import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingOption;
import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingStatus;
import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingStep;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;

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
        if (!option.isSupportedDuringOnboarding()) {
            throw WorkspaceExceptions.onboardingOptionNotSupported();
        }
        WorkspaceOnboardingState ready = ensureCanChooseOption();
        WorkspaceOnboardingStep nextStep = switch (option) {
            case CREATE_WORKSPACE -> WorkspaceOnboardingStep.CREATE_WORKSPACE;
            case JOIN_WITH_INVITATION -> WorkspaceOnboardingStep.ENTER_INVITATION_CODE;
            case REQUEST_TO_JOIN -> throw WorkspaceExceptions.onboardingOptionNotSupported();
        };
        return new WorkspaceOnboardingState(ready.id(), ready.userId(), ready.status(), nextStep, option,
                ready.targetWorkspaceId(), ready.createdOrganizationId(), ready.createdWorkspaceId(),
                ready.invitationId(), ready.joinRequestId(), ready.failureReason(), ready.completedAt(),
                ready.cancelledAt(), Instant.now(), ready.createdAt(), Instant.now());
    }

    public WorkspaceOnboardingState onWorkspaceCreated(UUID orgId, UUID workspaceId) {
        assertInProgress();
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.COMPLETED, WorkspaceOnboardingStep.COMPLETED,
                selectedOption, workspaceId, orgId, workspaceId,
                invitationId, joinRequestId, null, Instant.now(), null,
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState onInvitationAccepted(UUID workspaceId) {
        assertInProgress();
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.COMPLETED, WorkspaceOnboardingStep.COMPLETED,
                selectedOption, workspaceId, createdOrganizationId, createdWorkspaceId,
                invitationId, joinRequestId, null, Instant.now(), null,
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState onJoinRequestSubmitted(UUID requestId) {
        assertInProgress();
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.WAITING_FOR_APPROVAL, WorkspaceOnboardingStep.WAITING_JOIN_APPROVAL,
                selectedOption, targetWorkspaceId, createdOrganizationId, createdWorkspaceId,
                invitationId, requestId, failureReason, completedAt, cancelledAt,
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState onJoinRequestApproved() {
        assertWaitingForApproval();
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.COMPLETED, WorkspaceOnboardingStep.COMPLETED,
                selectedOption, targetWorkspaceId, createdOrganizationId, createdWorkspaceId,
                invitationId, joinRequestId, null, Instant.now(), null,
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState onJoinRequestRejected(String reason) {
        assertWaitingForApproval();
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.FAILED, WorkspaceOnboardingStep.FAILED,
                selectedOption, targetWorkspaceId, createdOrganizationId, createdWorkspaceId,
                invitationId, joinRequestId, reason, completedAt, cancelledAt,
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState cancel() {
        assertCancellable();
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.CANCELLED, WorkspaceOnboardingStep.CANCELLED,
                selectedOption, targetWorkspaceId, createdOrganizationId, createdWorkspaceId,
                invitationId, joinRequestId, failureReason, completedAt, Instant.now(),
                Instant.now(), createdAt, Instant.now());
    }

    public WorkspaceOnboardingState resetChoice() {
        if (status == WorkspaceOnboardingStatus.CANCELLED
                || status == WorkspaceOnboardingStatus.FAILED
                || status == WorkspaceOnboardingStatus.WAITING_FOR_APPROVAL) {
            return toChooseOptionSelection();
        }
        assertInProgress();
        if (!currentStep.isChoiceResettable()) {
            throw WorkspaceExceptions.onboardingInvalidStep();
        }
        return toChooseOptionSelection();
    }

    private WorkspaceOnboardingState ensureCanChooseOption() {
        if (status == WorkspaceOnboardingStatus.CANCELLED || status == WorkspaceOnboardingStatus.FAILED) {
            return toChooseOptionSelection();
        }
        assertInProgress();
        return this;
    }

    private WorkspaceOnboardingState toChooseOptionSelection() {
        return new WorkspaceOnboardingState(id, userId,
                WorkspaceOnboardingStatus.IN_PROGRESS, WorkspaceOnboardingStep.CHOOSE_WORKSPACE_OPTION,
                null, null, null, null, null, null, null, null, null,
                Instant.now(), createdAt, Instant.now());
    }

    private void assertInProgress() {
        if (status != WorkspaceOnboardingStatus.IN_PROGRESS) {
            throw WorkspaceExceptions.onboardingInvalidStep();
        }
    }

    private void assertWaitingForApproval() {
        if (status != WorkspaceOnboardingStatus.WAITING_FOR_APPROVAL) {
            throw WorkspaceExceptions.onboardingInvalidStep();
        }
    }

    private void assertCancellable() {
        if (status != WorkspaceOnboardingStatus.IN_PROGRESS
                && status != WorkspaceOnboardingStatus.WAITING_FOR_APPROVAL) {
            throw WorkspaceExceptions.onboardingInvalidStep();
        }
    }
}

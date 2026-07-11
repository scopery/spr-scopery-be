package com.company.scopery.modules.workspace.onboarding.infrastructure.mapper;

import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingOption;
import com.company.scopery.modules.workspace.onboarding.domain.model.WorkspaceOnboardingState;
import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingStatus;
import com.company.scopery.modules.workspace.onboarding.domain.enums.WorkspaceOnboardingStep;
import com.company.scopery.modules.workspace.onboarding.infrastructure.persistence.WorkspaceOnboardingStateJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceOnboardingStatePersistenceMapper {

    public WorkspaceOnboardingState toDomain(WorkspaceOnboardingStateJpaEntity e) {
        return new WorkspaceOnboardingState(
                e.getId(), e.getUserId(),
                WorkspaceOnboardingStatus.valueOf(e.getStatus()),
                WorkspaceOnboardingStep.valueOf(e.getCurrentStep()),
                e.getSelectedOption() != null ? WorkspaceOnboardingOption.valueOf(e.getSelectedOption()) : null,
                e.getTargetWorkspaceId(), e.getCreatedOrganizationId(), e.getCreatedWorkspaceId(),
                e.getInvitationId(), e.getJoinRequestId(), e.getFailureReason(),
                e.getCompletedAt(), e.getCancelledAt(), e.getLastSeenAt(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public WorkspaceOnboardingStateJpaEntity toJpaEntity(WorkspaceOnboardingState d) {
        WorkspaceOnboardingStateJpaEntity e = new WorkspaceOnboardingStateJpaEntity();
        e.setId(d.id());
        e.setUserId(d.userId());
        e.setStatus(d.status().name());
        e.setCurrentStep(d.currentStep().name());
        e.setSelectedOption(d.selectedOption() != null ? d.selectedOption().name() : null);
        e.setTargetWorkspaceId(d.targetWorkspaceId());
        e.setCreatedOrganizationId(d.createdOrganizationId());
        e.setCreatedWorkspaceId(d.createdWorkspaceId());
        e.setInvitationId(d.invitationId());
        e.setJoinRequestId(d.joinRequestId());
        e.setFailureReason(d.failureReason());
        e.setCompletedAt(d.completedAt());
        e.setCancelledAt(d.cancelledAt());
        e.setLastSeenAt(d.lastSeenAt());
        e.setCreatedAt(d.createdAt());
        return e;
    }
}

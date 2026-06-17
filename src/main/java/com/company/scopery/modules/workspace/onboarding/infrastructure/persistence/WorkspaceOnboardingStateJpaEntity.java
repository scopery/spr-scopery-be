package com.company.scopery.modules.workspace.onboarding.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = WorkspaceTableNames.ONBOARDING_STATE,
        uniqueConstraints = @UniqueConstraint(name = "uq_workspace_onboarding_state_user", columnNames = {"user_id"}),
        indexes = @Index(name = "idx_workspace_onboarding_state_status", columnList = "status")
)
public class WorkspaceOnboardingStateJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "current_step", nullable = false, length = 100)
    private String currentStep;

    @Column(name = "selected_option", length = 50)
    private String selectedOption;

    @Column(name = "target_workspace_id")
    private UUID targetWorkspaceId;

    @Column(name = "created_organization_id")
    private UUID createdOrganizationId;

    @Column(name = "created_workspace_id")
    private UUID createdWorkspaceId;

    @Column(name = "invitation_id")
    private UUID invitationId;

    @Column(name = "join_request_id")
    private UUID joinRequestId;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "last_seen_at", nullable = false)
    private Instant lastSeenAt;
}

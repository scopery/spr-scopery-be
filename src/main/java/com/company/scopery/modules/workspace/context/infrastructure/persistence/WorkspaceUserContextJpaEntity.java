package com.company.scopery.modules.workspace.context.infrastructure.persistence;

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
@Table(name = WorkspaceTableNames.USER_CONTEXT)
public class WorkspaceUserContextJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "current_workspace_id")
    private UUID currentWorkspaceId;

    @Column(name = "last_switched_at")
    private Instant lastSwitchedAt;

    @Column(name = "onboarding_completed_at")
    private Instant onboardingCompletedAt;
}

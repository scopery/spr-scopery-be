package com.company.scopery.modules.workspace.orgteam.infrastructure.persistence;

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
        name = WorkspaceTableNames.ORG_TEAM_WORKSPACE_ASSIGNMENT,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_org_team_workspace_assignment",
                columnNames = {"team_id", "workspace_id"}),
        indexes = {
                @Index(name = "idx_org_team_ws_assignment_team_id", columnList = "team_id"),
                @Index(name = "idx_org_team_ws_assignment_workspace_id", columnList = "workspace_id"),
                @Index(name = "idx_org_team_ws_assignment_status", columnList = "status")
        }
)
public class OrgTeamWorkspaceAssignmentJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "assigned_by")
    private UUID assignedBy;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}

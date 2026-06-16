package com.company.scopery.modules.workspace.team.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = WorkspaceTableNames.TEAM,
        uniqueConstraints = @UniqueConstraint(name = "uq_workspace_team_ws_code", columnNames = {"workspace_id", "code"}),
        indexes = {
                @Index(name = "idx_workspace_team_workspace_id", columnList = "workspace_id"),
                @Index(name = "idx_workspace_team_status", columnList = "status"),
                @Index(name = "idx_workspace_team_code", columnList = "code")
        }
)
public class WorkspaceTeamJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

}

package com.company.scopery.modules.workspace.member.infrastructure.persistence;

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
        name = WorkspaceTableNames.MEMBER,
        uniqueConstraints = @UniqueConstraint(name = "uq_workspace_member_ws_user", columnNames = {"workspace_id", "user_id"}),
        indexes = {
                @Index(name = "idx_workspace_member_workspace_id", columnList = "workspace_id"),
                @Index(name = "idx_workspace_member_status", columnList = "status")
        }
)
public class WorkspaceMemberJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

}

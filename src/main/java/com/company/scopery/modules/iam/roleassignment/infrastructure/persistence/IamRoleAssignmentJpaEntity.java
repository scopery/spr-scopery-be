package com.company.scopery.modules.iam.roleassignment.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.iam.shared.constant.IamTableNames;
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
        name = IamTableNames.IAM_ROLE_ASSIGNMENT,
        indexes = {
                @Index(name = "idx_iam_role_assignment_assignee_id",   columnList = "assignee_id"),
                @Index(name = "idx_iam_role_assignment_role_id",       columnList = "role_id"),
                @Index(name = "idx_iam_role_assignment_workspace_id",  columnList = "workspace_id"),
                @Index(name = "idx_iam_role_assignment_status",        columnList = "status")
        }
)
public class IamRoleAssignmentJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "assignee_type", nullable = false, length = 50)
    private String assigneeType;

    @Column(name = "assignee_id", nullable = false)
    private UUID assigneeId;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "assigned_by")
    private UUID assignedBy;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}

package com.company.scopery.modules.iam.permission.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.iam.shared.constant.IamTableNames;
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
        name = IamTableNames.IAM_PERMISSION_ACTION,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_iam_permission_action_permission_action",
                columnNames = {"permission_id", "action_code"}),
        indexes = {
                @Index(name = "idx_iam_permission_action_permission_id", columnList = "permission_id"),
                @Index(name = "idx_iam_permission_action_action_code", columnList = "action_code"),
                @Index(name = "idx_iam_permission_action_right_id", columnList = "right_id"),
                @Index(name = "idx_iam_permission_action_status", columnList = "status")
        }
)
public class IamPermissionActionDefinitionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "permission_id", nullable = false)
    private UUID permissionId;

    @Column(name = "action_code", nullable = false, length = 100)
    private String actionCode;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "right_id")
    private UUID rightId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}

package com.company.scopery.modules.iam.role.infrastructure.persistence;

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
        name = IamTableNames.IAM_ROLE,
        uniqueConstraints = @UniqueConstraint(name = "uq_iam_role_code", columnNames = "code"),
        indexes = {
                @Index(name = "idx_iam_role_code",         columnList = "code"),
                @Index(name = "idx_iam_role_status",       columnList = "status"),
                @Index(name = "idx_iam_role_role_scope",   columnList = "role_scope"),
                @Index(name = "idx_iam_role_role_source",  columnList = "role_source"),
                @Index(name = "idx_iam_role_deleted_at",   columnList = "deleted_at")
        }
)
public class IamRoleJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "role_scope", length = 50)
    private String roleScope;

    @Column(name = "role_source", length = 50)
    private String roleSource;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "parent_role_id")
    private UUID parentRoleId;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;
}

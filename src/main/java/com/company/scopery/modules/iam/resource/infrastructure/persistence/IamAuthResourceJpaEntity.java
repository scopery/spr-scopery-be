package com.company.scopery.modules.iam.resource.infrastructure.persistence;

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
        name = IamTableNames.IAM_AUTH_RESOURCE,
        uniqueConstraints = @UniqueConstraint(name = "uq_iam_auth_resource_code_type",
                columnNames = {"code", "resource_type"}),
        indexes = {
                @Index(name = "idx_iam_auth_resource_code",        columnList = "code"),
                @Index(name = "idx_iam_auth_resource_type",        columnList = "resource_type"),
                @Index(name = "idx_iam_auth_resource_status",      columnList = "status"),
                @Index(name = "idx_iam_auth_resource_ref_id",      columnList = "ref_id"),
                @Index(name = "idx_iam_auth_resource_owner",       columnList = "owner_user_id"),
                @Index(name = "idx_iam_auth_resource_workspace_id",columnList = "workspace_id"),
                @Index(name = "idx_iam_auth_resource_parent",      columnList = "parent_resource_id")
        }
)
public class IamAuthResourceJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "resource_type", nullable = false, length = 100)
    private String resourceType;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ref_id")
    private UUID refId;

    @Column(name = "owner_user_id")
    private UUID ownerUserId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "visibility", length = 100)
    private String visibility;

    @Column(name = "parent_resource_id")
    private UUID parentResourceId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Version @Column(name = "version", nullable = false)
    private Integer version;
}

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
        name = IamTableNames.IAM_PERMISSION,
        uniqueConstraints = @UniqueConstraint(name = "uq_iam_permission_code", columnNames = "code"),
        indexes = {
                @Index(name = "idx_iam_permission_code", columnList = "code"),
                @Index(name = "idx_iam_permission_module_code", columnList = "module_code"),
                @Index(name = "idx_iam_permission_scope_level", columnList = "resource_scope_level"),
                @Index(name = "idx_iam_permission_category", columnList = "permission_category"),
                @Index(name = "idx_iam_permission_risk_level", columnList = "risk_level"),
                @Index(name = "idx_iam_permission_status", columnList = "status")
        }
)
public class IamPermissionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "module_code", nullable = false, length = 100)
    private String moduleCode;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "resource_scope_level", nullable = false, length = 50)
    private String resourceScopeLevel;

    @Column(name = "data_access_policy", nullable = false, length = 50)
    private String dataAccessPolicy;

    @Column(name = "permission_category", nullable = false, length = 100)
    private String permissionCategory;

    @Column(name = "assignable_subject_types", nullable = false, length = 255)
    private String assignableSubjectTypes;

    @Column(name = "risk_level", nullable = false, length = 50)
    private String riskLevel;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}

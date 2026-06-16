package com.company.scopery.modules.iam.grant.infrastructure.persistence;

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
        name = IamTableNames.IAM_ACCESS_GRANT,
        indexes = {
                @Index(name = "idx_iam_access_grant_subject",      columnList = "subject_id"),
                @Index(name = "idx_iam_access_grant_resource",     columnList = "resource_id"),
                @Index(name = "idx_iam_access_grant_status",       columnList = "status"),
                @Index(name = "idx_iam_access_grant_effect",       columnList = "effect"),
                @Index(name = "idx_iam_access_grant_workspace_id", columnList = "workspace_id")
        }
)
public class IamAccessGrantJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "subject_type", nullable = false, length = 100)
    private String subjectType;

    @Column(name = "subject_id", nullable = false)
    private UUID subjectId;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "effect", nullable = false, length = 50)
    private String effect;

    @Column(name = "scope_type", length = 100)
    private String scopeType;

    @Column(name = "scope_ref_id")
    private UUID scopeRefId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "granted_by")
    private UUID grantedBy;

    @Column(name = "granted_at", nullable = false)
    private Instant grantedAt;
}

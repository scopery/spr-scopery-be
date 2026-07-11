package com.company.scopery.modules.workspace.workspace.infrastructure.persistence;

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
        name = WorkspaceTableNames.WORKSPACE,
        uniqueConstraints = @UniqueConstraint(name = "uq_workspace_workspace_org_code", columnNames = {"organization_id", "code"}),
        indexes = {
                @Index(name = "idx_workspace_workspace_status", columnList = "status"),
                @Index(name = "idx_workspace_workspace_org_id", columnList = "organization_id"),
                @Index(name = "idx_workspace_workspace_code", columnList = "code")
        }
)
public class WorkspaceJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Column(name = "default_visibility", nullable = false, length = 50)
    private String defaultVisibility;

    @Column(name = "join_policy", nullable = false, length = 50)
    private String joinPolicy;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Version @Column(name = "version", nullable = false)
    private Integer version;

}

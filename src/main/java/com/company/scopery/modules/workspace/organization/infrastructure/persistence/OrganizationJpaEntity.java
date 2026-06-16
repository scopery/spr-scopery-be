package com.company.scopery.modules.workspace.organization.infrastructure.persistence;

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
        name = WorkspaceTableNames.ORGANIZATION,
        uniqueConstraints = @UniqueConstraint(name = "uq_workspace_organization_code", columnNames = "code"),
        indexes = {
                @Index(name = "idx_workspace_organization_status", columnList = "status"),
                @Index(name = "idx_workspace_organization_code", columnList = "code")
        }
)
public class OrganizationJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

}

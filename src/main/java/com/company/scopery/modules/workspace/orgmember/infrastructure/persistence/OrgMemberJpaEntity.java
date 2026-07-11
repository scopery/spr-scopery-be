package com.company.scopery.modules.workspace.orgmember.infrastructure.persistence;

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
        name = WorkspaceTableNames.ORG_MEMBER,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_org_member_organization_user",
                columnNames = {"organization_id", "user_id"}),
        indexes = {
                @Index(name = "idx_org_member_organization_id", columnList = "organization_id"),
                @Index(name = "idx_org_member_user_id", columnList = "user_id"),
                @Index(name = "idx_org_member_status", columnList = "status")
        }
)
public class OrgMemberJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "membership_type", nullable = false, length = 50)
    private String membershipType;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "source", nullable = false, length = 100)
    private String source;

    @Column(name = "suspended_at")
    private Instant suspendedAt;

    @Column(name = "removed_at")
    private Instant removedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}

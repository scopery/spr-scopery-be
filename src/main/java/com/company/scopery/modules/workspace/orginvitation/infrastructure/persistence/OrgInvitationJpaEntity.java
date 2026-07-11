package com.company.scopery.modules.workspace.orginvitation.infrastructure.persistence;

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
        name = WorkspaceTableNames.ORG_INVITATION,
        uniqueConstraints = @UniqueConstraint(name = "uq_org_invitation_token", columnNames = {"token"}),
        indexes = {
                @Index(name = "idx_org_invitation_organization_id", columnList = "organization_id"),
                @Index(name = "idx_org_invitation_invitee_email", columnList = "invitee_email"),
                @Index(name = "idx_org_invitation_status", columnList = "status")
        }
)
public class OrgInvitationJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "invitee_email", nullable = false, length = 255)
    private String inviteeEmail;

    @Column(name = "invitee_user_id")
    private UUID inviteeUserId;

    @Column(name = "membership_type", nullable = false, length = 50)
    private String membershipType;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "invited_by", nullable = false)
    private UUID invitedBy;

    @Column(name = "token", nullable = false, length = 255)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "responded_at")
    private Instant respondedAt;
}

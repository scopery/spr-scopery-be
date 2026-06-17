package com.company.scopery.modules.workspace.invitation.infrastructure.persistence;

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
        name = WorkspaceTableNames.INVITATION,
        uniqueConstraints = @UniqueConstraint(name = "uq_workspace_invitation_code_hash", columnNames = {"invitation_code_hash"}),
        indexes = {
                @Index(name = "idx_workspace_invitation_workspace_id", columnList = "workspace_id"),
                @Index(name = "idx_workspace_invitation_status", columnList = "status")
        }
)
public class WorkspaceInvitationJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "created_by_user_id", nullable = false)
    private UUID createdByUserId;

    @Column(name = "invited_email")
    private String invitedEmail;

    @Column(name = "invitation_code_hash", nullable = false, length = 255)
    private String invitationCodeHash;

    @Column(name = "invitation_code_hint", length = 20)
    private String invitationCodeHint;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "used_count", nullable = false)
    private int usedCount;

    @Column(name = "expires_at")
    private Instant expiresAt;
}

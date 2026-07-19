package com.company.scopery.modules.clientportal.invite.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ClientPortalTableNames.INVITE) @Getter @Setter @NoArgsConstructor
public class ExternalPortalInviteJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="project_id") private UUID projectId;
    @Column(nullable=false) private String email;
    @Column(name="invite_token_hash", nullable=false) private String inviteTokenHash;
    @Column(nullable=false) private String status;
    @Column(name="expires_at", nullable=false) private Instant expiresAt;
    @Column(name="accepted_at") private Instant acceptedAt;
    @Column(name="invited_by") private UUID invitedBy;
    @Column(name="portal_account_id") private UUID portalAccountId;
    @Version private Integer version;
}

package com.company.scopery.modules.clientportal.account.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ClientPortalTableNames.ACCOUNT) @Getter @Setter @NoArgsConstructor
public class ExternalPortalAccountJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="contact_id") private UUID contactId;
    @Column(nullable=false) private String email;
    @Column(name="display_name") private String displayName;
    @Column(nullable=false) private String status;
    @Column(name="password_hash") private String passwordHash;
    @Column(name="last_login_at") private Instant lastLoginAt;
    @Column(name="activated_at") private Instant activatedAt;
    @Version private Integer version;
}

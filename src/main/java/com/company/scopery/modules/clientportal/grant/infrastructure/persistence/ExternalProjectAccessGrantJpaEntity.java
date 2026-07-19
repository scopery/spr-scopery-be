package com.company.scopery.modules.clientportal.grant.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalTableNames;
import jakarta.persistence.*; import lombok.*;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ClientPortalTableNames.ACCESS_GRANT) @Getter @Setter @NoArgsConstructor
public class ExternalProjectAccessGrantJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="project_id", nullable=false) private UUID projectId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="portal_account_id", nullable=false) private UUID portalAccountId;
    @Column(nullable=false) private String status;
    @Column(name="permission_policy_code") private String permissionPolicyCode;
    @Column(name="expires_at") private Instant expiresAt;
    @Version private Integer version;
}

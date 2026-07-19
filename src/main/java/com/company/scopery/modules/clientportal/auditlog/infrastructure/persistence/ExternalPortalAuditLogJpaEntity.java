package com.company.scopery.modules.clientportal.auditlog.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.clientportal.shared.constant.ClientPortalTableNames;
import jakarta.persistence.*; import lombok.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name = ClientPortalTableNames.AUDIT_LOG) @Getter @Setter @NoArgsConstructor
public class ExternalPortalAuditLogJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "portal_account_id") private UUID portalAccountId;
    @Column(nullable = false) private String action;
    @Column(name = "target_type") private String targetType;
    @Column(name = "target_id") private UUID targetId;
    @Column(columnDefinition = "TEXT") private String details;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;
    @Column(name = "ip_address") private String ipAddress;
}

package com.company.scopery.modules.integrationhub.connection.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.CONNECTION) @Getter @Setter @NoArgsConstructor
public class IntegrationConnectionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="provider_code", nullable=false) private String providerCode;
    @Column(name="connection_scope", nullable=false) private String connectionScope;
    @Column(nullable=false) private String name;
    @Column(name="credential_reference_id") private UUID credentialReferenceId;
    @Column(nullable=false) private String status;
    @Column(name="last_health_status") private String lastHealthStatus;
    @Column(name="last_health_checked_at") private Instant lastHealthCheckedAt;
    @Version private Integer version;
}

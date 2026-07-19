package com.company.scopery.modules.integrationhub.inbound.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.INBOUND_ENDPOINT) @Getter @Setter @NoArgsConstructor
public class InboundWebhookEndpointJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="connection_id", nullable=false) private UUID connectionId;
    @Column(name="endpoint_code", nullable=false) private String endpointCode;
    @Column(name="provider_code", nullable=false) private String providerCode;
    @Column(name="signing_secret_reference_id") private UUID signingSecretReferenceId;
    @Column(nullable=false) private String status;
    @Column(name="disabled_at") private Instant disabledAt;
    @Version private Integer version;
}

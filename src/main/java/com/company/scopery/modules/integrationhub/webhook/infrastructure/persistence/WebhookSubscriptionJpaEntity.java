package com.company.scopery.modules.integrationhub.webhook.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.WEBHOOK_SUB) @Getter @Setter @NoArgsConstructor
public class WebhookSubscriptionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="connection_id") private UUID connectionId;
    @Column(nullable=false) private String name;
    @Column(name="endpoint_url", nullable=false) private String endpointUrl;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="event_types_json", nullable=false, columnDefinition="jsonb") private String eventTypesJson;
    @Column(name="payload_version", nullable=false) private String payloadVersion;
    @Column(name="signing_secret_reference_id") private UUID signingSecretReferenceId;
    @Column(nullable=false) private String status;
    @Column(name="max_attempts", nullable=false) private Integer maxAttempts = 5;
    @Column(name="timeout_seconds", nullable=false) private Integer timeoutSeconds = 10;
    @Column(name="disabled_at") private Instant disabledAt;
    @Column(name="archived_at") private Instant archivedAt;
    @Version private Integer version;
}

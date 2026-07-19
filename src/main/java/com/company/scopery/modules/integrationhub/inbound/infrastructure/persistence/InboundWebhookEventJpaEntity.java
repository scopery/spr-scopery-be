package com.company.scopery.modules.integrationhub.inbound.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.INBOUND_EVENT) @Getter @Setter @NoArgsConstructor
public class InboundWebhookEventJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="inbound_endpoint_id", nullable=false) private UUID inboundEndpointId;
    @Column(name="provider_code", nullable=false) private String providerCode;
    @Column(name="external_event_id") private String externalEventId;
    @Column(name="event_type", nullable=false) private String eventType;
    @Column(nullable=false) private String status;
    @Column(name="payload_reference") private String payloadReference;
    @Column(name="payload_redacted_json", columnDefinition="jsonb") private String payloadRedactedJson;
    @Column(name="received_at", nullable=false) private Instant receivedAt;
    @Column(name="processed_at") private Instant processedAt;
    @Column(name="failure_code") private String failureCode;
    @Column(name="failure_message", columnDefinition="text") private String failureMessage;
    @Version private Integer version;
}

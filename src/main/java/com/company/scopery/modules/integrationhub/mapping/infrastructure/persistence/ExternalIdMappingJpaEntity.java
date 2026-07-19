package com.company.scopery.modules.integrationhub.mapping.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.EXTERNAL_ID_MAPPING) @Getter @Setter @NoArgsConstructor
public class ExternalIdMappingJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="connection_id", nullable=false) private UUID connectionId;
    @Column(name="provider_code", nullable=false) private String providerCode;
    @Column(name="external_object_type", nullable=false) private String externalObjectType;
    @Column(name="external_id", nullable=false) private String externalId;
    @Column(name="scopery_object_type", nullable=false) private String scoperyObjectType;
    @Column(name="scopery_object_id", nullable=false) private UUID scoperyObjectId;
    @Column(name="last_synced_at") private Instant lastSyncedAt;
    @Column(name="sync_state") private String syncState;
    @Version private Integer version;
}

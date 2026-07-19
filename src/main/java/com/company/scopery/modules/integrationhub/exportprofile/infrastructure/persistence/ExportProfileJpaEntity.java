package com.company.scopery.modules.integrationhub.exportprofile.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.EXPORT_PROFILE) @Getter @Setter @NoArgsConstructor
public class ExportProfileJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="connection_id") private UUID connectionId;
    @Column(name="profile_code", nullable=false) private String profileCode;
    @Column(nullable=false) private String name;
    @Column(name="export_format", nullable=false) private String exportFormat;
    @Column(name="target_destination", nullable=false) private String targetDestination;
    @Column(name="object_scope", nullable=false) private String objectScope;
    @Column(name="columns_json", columnDefinition="jsonb") private String columnsJson;
    @Column(name="filters_json", columnDefinition="jsonb") private String filtersJson;
    @Column(name="masking_policy") private String maskingPolicy;
    @Column(nullable=false) private String status;
    @Column(name="archived_at") private Instant archivedAt;
    @Version private Integer version;
}

package com.company.scopery.modules.integrationhub.mapping.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.integrationhub.shared.constant.IntegrationTableNames;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
import java.time.Instant; import java.util.UUID;
@Entity @Table(name = IntegrationTableNames.MAPPING_PROFILE) @Getter @Setter @NoArgsConstructor
public class DataMappingProfileJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(name="connection_id") private UUID connectionId;
    @Column(name="mapping_code", nullable=false) private String mappingCode;
    @Column(nullable=false) private String name;
    @Column(name="target_object_type", nullable=false) private String targetObjectType;
    @Column(name="source_format", nullable=false) private String sourceFormat;
    @Column(name="mapping_json", nullable=false, columnDefinition="jsonb") private String mappingJson;
    @Column(name="validation_rules_json", columnDefinition="jsonb") private String validationRulesJson;
    @Column(nullable=false) private String status;
    @Column(name="archived_at") private Instant archivedAt;
    @Version private Integer version;
}

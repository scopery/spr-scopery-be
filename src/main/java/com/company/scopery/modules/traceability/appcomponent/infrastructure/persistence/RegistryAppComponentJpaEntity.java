package com.company.scopery.modules.traceability.appcomponent.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*; import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;
@Entity @Table(name = TraceabilityTableNames.APP_COMPONENT) @Getter @Setter @NoArgsConstructor
public class RegistryAppComponentJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name="application_id", nullable=false) private UUID applicationId;
    @Column(name="workspace_id", nullable=false) private UUID workspaceId;
    @Column(nullable=false) private String code;
    @Column(nullable=false) private String name;
    @Column(columnDefinition="text") private String description;
    @Column(name="component_type") private String componentType;
    @Column(name="option_source_type", nullable=false) private String optionSourceType;
    @Column(name="source_entity_id") private UUID sourceEntityId;
    @Column(name="source_value_column") private String sourceValueColumn;
    @Column(name="source_label_column") private String sourceLabelColumn;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name="source_filter_json", columnDefinition="jsonb") private String sourceFilterJson;
    @Column(nullable=false) private String status;
    @Version private Integer version;
}

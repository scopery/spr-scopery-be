package com.company.scopery.modules.traceability.dataentity.infrastructure.persistence;
import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
@Entity @Table(name = TraceabilityTableNames.DATA_ENTITY) @Getter @Setter @NoArgsConstructor
public class RegistryDataEntityJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "application_id", nullable = false) private UUID applicationId;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "module_id") private UUID moduleId;
    @Column(nullable = false) private String code;
    @Column(nullable = false) private String name;
    @Column(columnDefinition = "text") private String description;
    @Column(name = "table_name") private String tableName;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}

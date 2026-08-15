package com.company.scopery.modules.traceability.dataentityrelation.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.DATA_ENTITY_RELATION)
@Getter @Setter @NoArgsConstructor
public class RegistryDataEntityRelationJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "source_entity_id", nullable = false) private UUID sourceEntityId;
    @Column(name = "target_entity_id", nullable = false) private UUID targetEntityId;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "relation_type", nullable = false) private String relationType;
    @Column(name = "source_column") private String sourceColumn;
    @Column private String label;
    @Column(columnDefinition = "text") private String note;
    @Column(nullable = false) private String status;
    @Version private Integer version;
}

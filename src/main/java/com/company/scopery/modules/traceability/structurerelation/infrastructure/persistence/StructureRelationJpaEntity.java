package com.company.scopery.modules.traceability.structurerelation.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = TraceabilityTableNames.STRUCTURE_RELATION)
@Getter
@Setter
@NoArgsConstructor
public class StructureRelationJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "application_id", nullable = false)
    private UUID applicationId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "from_node_type", nullable = false)
    private String fromNodeType;

    @Column(name = "from_node_id", nullable = false)
    private UUID fromNodeId;

    @Column(name = "to_node_type", nullable = false)
    private String toNodeType;

    @Column(name = "to_node_id", nullable = false)
    private UUID toNodeId;

    @Column(name = "relation_type", nullable = false)
    private String relationType;

    @Version
    private Integer version;
}

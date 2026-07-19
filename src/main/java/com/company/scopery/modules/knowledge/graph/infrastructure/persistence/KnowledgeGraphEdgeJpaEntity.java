package com.company.scopery.modules.knowledge.graph.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = KnowledgeTableNames.KNOWLEDGE_GRAPH_EDGE)
@Getter @Setter @NoArgsConstructor
public class KnowledgeGraphEdgeJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "from_node_id", nullable = false)
    private UUID fromNodeId;

    @Column(name = "to_node_id", nullable = false)
    private UUID toNodeId;

    @Column(name = "edge_type", nullable = false, length = 60)
    private String edgeType;

    @Column(name = "source_ref_id")
    private UUID sourceRefId;

    @Column(name = "edge_status", nullable = false, length = 20)
    private String edgeStatus;

    @Version
    private Long version;
}

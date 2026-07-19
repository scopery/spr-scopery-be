package com.company.scopery.modules.knowledge.graph.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = KnowledgeTableNames.KNOWLEDGE_GRAPH_NODE)
@Getter @Setter @NoArgsConstructor
public class KnowledgeGraphNodeJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "node_type", nullable = false, length = 40)
    private String nodeType;

    @Column(name = "source_ref_id", nullable = false)
    private UUID sourceRefId;

    @Column(name = "source_version_ref_id", nullable = false)
    private UUID sourceVersionRefId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(name = "permission_signature", nullable = false, length = 96)
    private String permissionSignature;

    @Column(name = "acl_tokens", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String aclTokens;

    @Column(name = "node_status", nullable = false, length = 20)
    private String nodeStatus;

    @Version
    private Long version;
}

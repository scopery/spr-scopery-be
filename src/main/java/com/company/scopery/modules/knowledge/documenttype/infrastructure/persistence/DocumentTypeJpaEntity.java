package com.company.scopery.modules.knowledge.documenttype.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = KnowledgeTableNames.KNOWLEDGE_DOCUMENT_TYPE,
        indexes = {
                @Index(name = "idx_knowledge_document_type_status",       columnList = "status"),
                @Index(name = "idx_knowledge_document_type_scope",        columnList = "document_scope"),
                @Index(name = "idx_knowledge_document_type_workspace_id", columnList = "workspace_id"),
                @Index(name = "idx_knowledge_document_type_deleted_at",   columnList = "deleted_at")
        }
)
public class DocumentTypeJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "document_scope", nullable = false, length = 50)
    private String documentScope;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;
}

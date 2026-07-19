package com.company.scopery.modules.knowledge.documenttype.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = KnowledgeTableNames.KNOWLEDGE_DOCUMENT_TYPE,
        indexes = {
                @Index(name = "idx_knowledge_document_type_status", columnList = "status"),
                @Index(name = "idx_knowledge_document_type_scope", columnList = "document_scope"),
                @Index(name = "idx_knowledge_document_type_workspace_id", columnList = "workspace_id"),
                @Index(name = "idx_knowledge_document_type_organization_id", columnList = "organization_id"),
                @Index(name = "idx_knowledge_document_type_deleted_at", columnList = "deleted_at"),
                @Index(name = "idx_knowledge_document_type_archived_at", columnList = "archived_at")
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

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "default_classification", length = 50)
    private String defaultClassification;

    @Column(name = "default_review_cycle_days")
    private Integer defaultReviewCycleDays;

    @Column(name = "default_template_code", length = 150)
    private String defaultTemplateCode;

    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "metadata_schema_json", columnDefinition = "jsonb")
    private String metadataSchemaJson;

    @Column(name = "built_in", nullable = false)
    private boolean builtIn;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "archived_by")
    private UUID archivedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @Column(name = "version", nullable = false)
    private int version;
}

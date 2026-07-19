package com.company.scopery.modules.knowledge.documenttypefield.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = KnowledgeTableNames.KNOWLEDGE_DOCUMENT_TYPE_FIELD,
        indexes = {
                @Index(name = "idx_knowledge_document_type_field_document_type_id", columnList = "document_type_id"),
                @Index(name = "idx_knowledge_document_type_field_status", columnList = "status")
        }
)
public class DocumentTypeFieldJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "document_type_id", nullable = false)
    private UUID documentTypeId;

    @Column(name = "field_key", nullable = false, length = 100)
    private String fieldKey;

    @Column(name = "label", nullable = false, length = 255)
    private String label;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "data_type", nullable = false, length = 50)
    private String dataType;

    @Column(name = "required", nullable = false)
    private boolean required;

    @Column(name = "system_field", nullable = false)
    private boolean systemField;

    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "options_json", columnDefinition = "jsonb")
    private String optionsJson;

    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "validation_json", columnDefinition = "jsonb")
    private String validationJson;

    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "default_value_json", columnDefinition = "jsonb")
    private String defaultValueJson;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "version", nullable = false)
    private int version;
}

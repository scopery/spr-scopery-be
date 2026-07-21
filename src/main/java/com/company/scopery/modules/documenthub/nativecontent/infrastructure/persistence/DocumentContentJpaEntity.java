package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.CONTENT,
        uniqueConstraints = @UniqueConstraint(name = "uq_documenthub_content_document_id", columnNames = "document_id"))
@Getter @Setter @NoArgsConstructor
public class DocumentContentJpaEntity extends AuditableJpaEntity {

    @Id
    private UUID id;

    @Column(name = "document_id", nullable = false, updatable = false)
    private UUID documentId;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "schema_version")
    private Integer schemaVersion;

    @Column(name = "revision_no", nullable = false)
    private long revisionNo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ast", nullable = false, columnDefinition = "jsonb")
    private String ast;

    @Column(name = "plain_text", columnDefinition = "text")
    private String plainText;

    @Column(name = "word_count", nullable = false)
    private int wordCount;

    @Column(name = "character_count", nullable = false)
    private int characterCount;

    @Column(name = "checksum", length = 128)
    private String checksum;

    @Column(name = "last_saved_at")
    private Instant lastSavedAt;

    @Column(name = "last_saved_by")
    private UUID lastSavedBy;

    @Version
    @Column(name = "version", nullable = false)
    private int version;
}

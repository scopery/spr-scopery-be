package com.company.scopery.modules.documenthub.nativecontent.infrastructure.persistence;

import com.company.scopery.modules.documenthub.shared.constant.DocumentHubTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = DocumentHubTableNames.REVISION,
        uniqueConstraints = @UniqueConstraint(name = "uq_documenthub_revision_doc_rev", columnNames = {"document_id", "revision_no"}))
@Getter @Setter @NoArgsConstructor
public class DocumentRevisionJpaEntity {

    @Id
    private UUID id;

    @Column(name = "document_id", nullable = false, updatable = false)
    private UUID documentId;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "revision_no", nullable = false, updatable = false)
    private long revisionNo;

    @Column(name = "revision_type", nullable = false, updatable = false, length = 32)
    private String revisionType;

    @Column(name = "ast", nullable = false, updatable = false, columnDefinition = "jsonb")
    private String ast;

    @Column(name = "plain_text", updatable = false, columnDefinition = "text")
    private String plainText;

    @Column(name = "checksum", length = 128, updatable = false)
    private String checksum;

    @Column(name = "schema_version", updatable = false)
    private Integer schemaVersion;

    @Column(name = "word_count", nullable = false, updatable = false)
    private int wordCount;

    @Column(name = "character_count", nullable = false, updatable = false)
    private int characterCount;

    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

package com.company.scopery.modules.knowledge.source.infrastructure.persistence;

import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = KnowledgeTableNames.KNOWLEDGE_CHUNK)
@Getter @Setter @NoArgsConstructor
public class KnowledgeChunkJpaEntity {
    @Id private UUID id;
    @Column(name = "source_id", nullable = false) private UUID sourceId;
    @Column(name = "projection_id", nullable = false) private UUID projectionId;
    @Column(name = "chunk_ordinal", nullable = false) private Integer chunkOrdinal;
    @Column(name = "strategy_version", nullable = false, length = 40) private String strategyVersion;
    @Column(name = "chunk_type", nullable = false, length = 40) private String chunkType;
    @Column(name = "heading_path", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON) private String headingPath;
    @Column(name = "plain_text", nullable = false, columnDefinition = "text") private String plainText;
    @Column(name = "token_count", nullable = false) private Integer tokenCount;
    @Column(name = "start_code_point", nullable = false) private Integer startCodePoint;
    @Column(name = "end_code_point", nullable = false) private Integer endCodePoint;
    @Column(name = "content_hash", nullable = false, length = 64) private String contentHash;
    @Column(name = "metadata", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON) private String metadata;
    @Column(name = "is_current", nullable = false) private Boolean isCurrent;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    // Pgvector search fields — written exclusively via native SQL in PostgresKnowledgeIndexService
    @Column(name = "title", insertable = false, updatable = false) private String title;
    @Column(name = "language", length = 20, insertable = false, updatable = false) private String language;
    @Column(name = "workspace_id", insertable = false, updatable = false) private UUID workspaceId;
    @Column(name = "project_id", insertable = false, updatable = false) private UUID projectId;
    @Column(name = "classification", length = 50, insertable = false, updatable = false) private String classification;
    @Column(name = "source_type", length = 50, insertable = false, updatable = false) private String sourceType;
    @Column(name = "source_status", length = 50, insertable = false, updatable = false) private String sourceStatus;
    @Column(name = "app_route", insertable = false, updatable = false) private String appRoute;
    @Column(name = "indexed_at", insertable = false, updatable = false) private Instant indexedAt;
}

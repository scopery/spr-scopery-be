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
@Table(name = KnowledgeTableNames.KNOWLEDGE_PROJECTION)
@Getter @Setter @NoArgsConstructor
public class KnowledgeProjectionJpaEntity {
    @Id private UUID id;
    @Column(name = "source_id", nullable = false) private UUID sourceId;
    @Column(name = "projection_version", nullable = false) private Integer projectionVersion;
    @Column(name = "extractor_code", nullable = false, length = 80) private String extractorCode;
    @Column(name = "extractor_version", nullable = false, length = 40) private String extractorVersion;
    @Column(name = "normalization_version", nullable = false, length = 40) private String normalizationVersion;
    @Column(name = "plain_text", nullable = false, columnDefinition = "text") private String plainText;
    @Column(name = "structured_metadata", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON) private String structuredMetadata;
    @Column(name = "heading_index", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON) private String headingIndex;
    @Column(name = "content_hash", nullable = false, length = 64) private String contentHash;
    @Column(name = "projection_status", nullable = false, length = 24) private String projectionStatus;
    @Column(name = "failure_code", length = 80) private String failureCode;
    @Column(name = "failure_message_redacted", length = 500) private String failureMessageRedacted;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "created_by") private UUID createdBy;
}

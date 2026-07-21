package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantTableNames;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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
    name = AiAssistantTableNames.MESSAGE_CITATION,
    indexes = {
        @Index(name = "idx_aiassistant_message_citation_message_id", columnList = "message_id"),
        @Index(name = "idx_aiassistant_message_citation_source_ref_id", columnList = "source_ref_id")
    }
)
public class AiMessageCitationJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "message_id", nullable = false, updatable = false)
    private UUID messageId;

    @Column(name = "ordinal", nullable = false)
    private int ordinal;

    @Column(name = "retrieval_trace_id")
    private UUID retrievalTraceId;

    @Column(name = "knowledge_chunk_id")
    private UUID knowledgeChunkId;

    @Column(name = "source_type", nullable = false, length = 100)
    private String sourceType;

    @Column(name = "source_ref_id", nullable = false)
    private UUID sourceRefId;

    @Column(name = "source_version_ref_id")
    private UUID sourceVersionRefId;

    @Column(name = "title", length = 500)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "heading_path", columnDefinition = "jsonb")
    private String headingPath;

    @Column(name = "quoted_fragment", columnDefinition = "TEXT")
    private String quotedFragment;

    @Column(name = "app_route", length = 500)
    private String appRoute;

    @Column(name = "permission_signature", length = 500)
    private String permissionSignature;

    @Column(name = "access_validation_result", length = 50)
    private String accessValidationResult;

    @Column(name = "access_validated_at", nullable = false)
    private Instant accessValidatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

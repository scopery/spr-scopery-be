package com.company.scopery.modules.knowledge.indexing.infrastructure.persistence;

import com.company.scopery.modules.knowledge.shared.constant.KnowledgeTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = KnowledgeTableNames.KNOWLEDGE_INDEX_JOB)
@Getter @Setter @NoArgsConstructor
public class KnowledgeIndexJobJpaEntity {

    @Id
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(name = "embedding_profile_id", nullable = false)
    private UUID embeddingProfileId;

    @Column(name = "job_type", nullable = false, length = 40)
    private String jobType;

    @Column(name = "job_status", nullable = false, length = 24)
    private String jobStatus;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 200)
    private String idempotencyKey;

    @Column(name = "target_index_name", length = 255)
    private String targetIndexName;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    @Column(name = "processed_count", nullable = false)
    private Integer processedCount;

    @Column(name = "success_count", nullable = false)
    private Integer successCount;

    @Column(name = "failure_count", nullable = false)
    private Integer failureCount;

    @Column(name = "error_code", length = 80)
    private String errorCode;

    @Column(name = "error_message_redacted", length = 500)
    private String errorMessageRedacted;

    @Column(name = "queued_at", nullable = false)
    private Instant queuedAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "created_by")
    private UUID createdBy;
}

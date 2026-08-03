package com.company.scopery.modules.traceability.aimapping.run.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.traceability.aimapping.shared.constant.AiMappingTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
        name = AiMappingTableNames.MAPPING_RUN,
        indexes = {
                @Index(name = "idx_ai_mapping_run_project_status", columnList = "project_id, status"),
                @Index(name = "idx_ai_mapping_run_requested_by", columnList = "requested_by")
        }
)
public class MappingRunJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    @Column(name = "relation_type", nullable = false, length = 50, updatable = false)
    private String relationType;

    @Column(name = "scope", nullable = false, length = 50, updatable = false)
    private String scope;

    @Column(name = "model_deployment_id")
    private UUID modelDeploymentId;

    @Column(name = "prompt_key", nullable = false, length = 100)
    private String promptKey;

    @Column(name = "prompt_version", nullable = false)
    private int promptVersion;

    @Column(name = "summary_version", nullable = false)
    private int summaryVersion;

    @Column(name = "candidate_limit", nullable = false)
    private int candidateLimit;

    @Column(name = "requested_by")
    private UUID requestedBy;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "source_count")
    private Integer sourceCount;

    @Column(name = "processed_source_count", nullable = false)
    private int processedSourceCount;

    @Column(name = "suggestion_count")
    private Integer suggestionCount;

    @Column(name = "token_usage_json", columnDefinition = "TEXT")
    private String tokenUsageJson;
}

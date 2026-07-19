package com.company.scopery.modules.aiplanning.suggestion.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = AiPlanningTableNames.SUGGESTION)
@Getter
@Setter
@NoArgsConstructor
public class AiPlanningSuggestionJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "planning_run_id", nullable = false) private UUID planningRunId;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "workspace_id", nullable = false) private UUID workspaceId;
    @Column(name = "suggestion_type", nullable = false) private String suggestionType;
    @Column(nullable = false) private String title;
    @Column(columnDefinition = "text") private String summary;
    @Column(columnDefinition = "text") private String rationale;
    @Column(name = "confidence_label") private String confidenceLabel;
    @Column(nullable = false) private String status;
    @Column(name = "source_references_json", columnDefinition = "text") private String sourceReferencesJson;
    @Column(name = "reviewed_at") private Instant reviewedAt;
    @Column(name = "reviewed_by") private UUID reviewedBy;
    @Column(name = "applied_at") private Instant appliedAt;
    @Column(name = "applied_by") private UUID appliedBy;
    @Column(name = "rejected_at") private Instant rejectedAt;
    @Column(name = "rejected_by") private UUID rejectedBy;
    @Column(name = "rejection_reason", columnDefinition = "text") private String rejectionReason;
    @Version private Integer version;
}

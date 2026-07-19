package com.company.scopery.modules.aiplanning.suggestionitem.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiplanning.shared.constant.AiPlanningTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = AiPlanningTableNames.SUGGESTION_ITEM)
@Getter @Setter @NoArgsConstructor
public class AiPlanningSuggestionItemJpaEntity extends AuditableJpaEntity {
    @Id private UUID id;
    @Column(name = "suggestion_id", nullable = false) private UUID suggestionId;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "item_type", nullable = false) private String itemType;
    @Column(name = "target_type") private String targetType;
    @Column(name = "target_id") private UUID targetId;
    @Column(nullable = false) private String operation;
    @Column(nullable = false) private String title;
    @Column(columnDefinition = "text") private String description;
    @Column(name = "proposed_payload_json", nullable = false, columnDefinition = "text") private String proposedPayloadJson;
    @Column(columnDefinition = "text") private String rationale;
    @Column(name = "confidence_label") private String confidenceLabel;
    @Column(nullable = false) private String status;
    @Column(name = "apply_action") private String applyAction;
    @Column(name = "apply_result_json", columnDefinition = "text") private String applyResultJson;
    @Column(name = "reviewed_at") private Instant reviewedAt;
    @Column(name = "reviewed_by") private UUID reviewedBy;
    @Column(name = "applied_at") private Instant appliedAt;
    @Column(name = "applied_by") private UUID appliedBy;
    @Version private Integer version;
}

package com.company.scopery.modules.elicitation.suggestion.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.elicitation.shared.constant.ElicitationTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = ElicitationTableNames.SUGGESTION_ITEM,
        indexes = {
                @Index(name = "idx_elicitation_suggestion_item_suggestion", columnList = "suggestion_id"),
                @Index(name = "idx_elicitation_suggestion_item_status", columnList = "suggestion_id, status")
        }
)
public class ElicitationSuggestionItemJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "suggestion_id", nullable = false, updatable = false)
    private UUID suggestionId;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "target_entity_type", length = 50)
    private String targetEntityType;

    @Column(name = "target_entity_id")
    private UUID targetEntityId;

    @Column(name = "target_entity_name", columnDefinition = "TEXT")
    private String targetEntityName;

    @Column(name = "rationale", nullable = false, columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "changes_json", columnDefinition = "TEXT")
    private String changesJson;

    @Column(name = "precondition_actions_json", columnDefinition = "TEXT")
    private String preconditionActionsJson;

    @Column(name = "estimated_impact", nullable = false, length = 50)
    private String estimatedImpact;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "executed_at")
    private Instant executedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "rollback_data_json", columnDefinition = "TEXT")
    private String rollbackDataJson;

    @Override
    public UUID getId() { return id; }
}

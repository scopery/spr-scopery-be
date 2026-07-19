package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = AiRecommendationTableNames.SUGGESTION_ITEM,
        indexes = {
                @Index(name = "idx_ai_recommendation_suggestion_item_suggestion_id", columnList = "suggestion_id"),
                @Index(name = "idx_ai_recommendation_suggestion_item_ordinal",       columnList = "suggestion_id,ordinal")
        }
)
public class AiSuggestionItemJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "suggestion_id", nullable = false)
    private UUID suggestionId;

    @Column(name = "ordinal", nullable = false)
    private int ordinal;

    @Column(name = "operation", nullable = false, length = 100)
    private String operation;

    @Column(name = "target_entity_type", length = 100)
    private String targetEntityType;

    @Column(name = "target_entity_id")
    private UUID targetEntityId;

    @Column(name = "expected_target_version_token", length = 255)
    private String expectedTargetVersionToken;

    @Column(name = "schema_code", length = 100)
    private String schemaCode;

    @Column(name = "schema_version", nullable = false)
    private int schemaVersion;

    @Column(name = "proposed_payload", columnDefinition = "TEXT")
    private String proposedPayload;

    @Column(name = "masked_before_snapshot", columnDefinition = "TEXT")
    private String maskedBeforeSnapshot;

    @Column(name = "payload_hash", length = 255)
    private String payloadHash;

    @Column(name = "required_target_capability_code", length = 100)
    private String requiredTargetCapabilityCode;

    @Column(name = "confirmation_required", nullable = false)
    private boolean confirmationRequired;

    @Column(name = "baseline_impact", length = 100)
    private String baselineImpact;
}

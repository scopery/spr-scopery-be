package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.airecommendation.shared.constant.AiRecommendationTableNames;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = AiRecommendationTableNames.POLICY,
        uniqueConstraints = @UniqueConstraint(name = "uq_ai_recommendation_policy_code", columnNames = {"code"}),
        indexes = {
                @Index(name = "idx_ai_recommendation_policy_code",   columnList = "code"),
                @Index(name = "idx_ai_recommendation_policy_status", columnList = "status")
        }
)
public class AiRecommendationPolicyJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100, updatable = false)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @Column(name = "scope_type", length = 100)
    private String scopeType;

    @Column(name = "trigger_modes", columnDefinition = "TEXT")
    private String triggerModes;

    @Column(name = "pack_codes", columnDefinition = "TEXT")
    private String packCodes;

    @Column(name = "llm_enrichment_enabled", nullable = false)
    private boolean llmEnrichmentEnabled;

    @Column(name = "min_confidence", precision = 5, scale = 4)
    private BigDecimal minConfidence;

    @Column(name = "default_severity", length = 100)
    private String defaultSeverity;

    @Column(name = "default_cooldown_minutes", nullable = false)
    private int defaultCooldownMinutes;

    @Column(name = "max_suggestions_per_run", nullable = false)
    private int maxSuggestionsPerRun;

    @Column(name = "publish_to_inbox", nullable = false)
    private boolean publishToInbox;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}

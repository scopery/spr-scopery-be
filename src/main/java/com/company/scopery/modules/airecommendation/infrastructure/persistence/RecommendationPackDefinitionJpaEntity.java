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

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = AiRecommendationTableNames.PACK_DEFINITION,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ai_recommendation_pack_definition_code_version",
                columnNames = {"code", "version"}
        ),
        indexes = {
                @Index(name = "idx_ai_recommendation_pack_def_code",   columnList = "code"),
                @Index(name = "idx_ai_recommendation_pack_def_status", columnList = "status")
        }
)
public class RecommendationPackDefinitionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "detector_codes", columnDefinition = "TEXT")
    private String detectorCodes;

    @Column(name = "allowed_trigger_modes", columnDefinition = "TEXT")
    private String allowedTriggerModes;

    @Column(name = "llm_enrichment_enabled", nullable = false)
    private boolean llmEnrichmentEnabled;

    @Column(name = "default_cooldown_minutes", nullable = false)
    private int defaultCooldownMinutes;

    @Column(name = "default_expiry_minutes", nullable = false)
    private int defaultExpiryMinutes;

    @Column(name = "max_suggestions_per_run", nullable = false)
    private int maxSuggestionsPerRun;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @Version
    @Column(name = "version_lock", nullable = false)
    private long versionLock;
}

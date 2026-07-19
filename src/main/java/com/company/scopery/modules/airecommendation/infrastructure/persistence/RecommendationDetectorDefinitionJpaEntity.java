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
        name = AiRecommendationTableNames.DETECTOR_DEFINITION,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ai_recommendation_detector_definition_code_version",
                columnNames = {"code", "version"}
        ),
        indexes = {
                @Index(name = "idx_ai_recommendation_detector_def_code",      columnList = "code"),
                @Index(name = "idx_ai_recommendation_detector_def_pack_code", columnList = "pack_code"),
                @Index(name = "idx_ai_recommendation_detector_def_status",    columnList = "status")
        }
)
public class RecommendationDetectorDefinitionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "pack_code", nullable = false, length = 100)
    private String packCode;

    @Column(name = "suggestion_type", length = 100)
    private String suggestionType;

    @Column(name = "schema_code", length = 100)
    private String schemaCode;

    @Column(name = "schema_version", nullable = false)
    private int schemaVersion;

    @Column(name = "execution_method", nullable = false, length = 100)
    private String executionMethod;

    @Column(name = "default_confidence", precision = 5, scale = 4)
    private BigDecimal defaultConfidence;

    @Column(name = "default_severity", length = 100)
    private String defaultSeverity;

    @Column(name = "default_expiry_minutes", nullable = false)
    private int defaultExpiryMinutes;

    @Column(name = "default_cooldown_minutes", nullable = false)
    private int defaultCooldownMinutes;

    @Column(name = "non_suppressible", nullable = false)
    private boolean nonSuppressible;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @Version
    @Column(name = "version_lock", nullable = false)
    private long versionLock;
}

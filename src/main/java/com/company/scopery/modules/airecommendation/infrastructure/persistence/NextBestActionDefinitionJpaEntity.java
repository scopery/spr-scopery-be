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
        name = AiRecommendationTableNames.NBA_DEFINITION,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ai_recommendation_nba_definition_code_version",
                columnNames = {"code", "version"}
        ),
        indexes = {
                @Index(name = "idx_ai_recommendation_nba_def_code",         columnList = "code"),
                @Index(name = "idx_ai_recommendation_nba_def_status",       columnList = "status"),
                @Index(name = "idx_ai_recommendation_nba_def_action_kind",  columnList = "action_kind")
        }
)
public class NextBestActionDefinitionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "label", nullable = false, length = 255)
    private String label;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "action_kind", nullable = false, length = 100)
    private String actionKind;

    @Column(name = "applicable_suggestion_types", columnDefinition = "TEXT")
    private String applicableSuggestionTypes;

    @Column(name = "required_authority_code", length = 100)
    private String requiredAuthorityCode;

    @Column(name = "required_target_capability_code", length = 100)
    private String requiredTargetCapabilityCode;

    @Column(name = "phase44_tool_code", length = 100)
    private String phase44ToolCode;

    @Column(name = "phase44_tool_version", length = 100)
    private String phase44ToolVersion;

    @Column(name = "risk_level", length = 100)
    private String riskLevel;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @Version
    @Column(name = "version_lock", nullable = false)
    private long versionLock;
}

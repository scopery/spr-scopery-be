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
        name = AiRecommendationTableNames.SCHEMA_DEFINITION,
        uniqueConstraints = @UniqueConstraint(
                name = "uq_ai_recommendation_schema_definition_code_version",
                columnNames = {"code", "schema_version"}
        ),
        indexes = {
                @Index(name = "idx_ai_recommendation_schema_def_code",          columnList = "code"),
                @Index(name = "idx_ai_recommendation_schema_def_status",        columnList = "status"),
                @Index(name = "idx_ai_recommendation_schema_def_suggestion_type", columnList = "suggestion_type")
        }
)
public class SuggestionSchemaDefinitionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "schema_version", nullable = false)
    private int schemaVersion;

    @Column(name = "suggestion_type", length = 100)
    private String suggestionType;

    @Column(name = "operation", length = 100)
    private String operation;

    @Column(name = "target_entity_type", length = 100)
    private String targetEntityType;

    @Column(name = "required_target_capability_code", length = 100)
    private String requiredTargetCapabilityCode;

    @Column(name = "confirmation_required", nullable = false)
    private boolean confirmationRequired;

    @Column(name = "baseline_impact", length = 100)
    private String baselineImpact;

    @Column(name = "sensitive_field_paths", columnDefinition = "TEXT")
    private String sensitiveFieldPaths;

    @Column(name = "json_schema", columnDefinition = "TEXT")
    private String jsonSchema;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @Column(name = "immutable_after_activation", nullable = false)
    private boolean immutableAfterActivation;

    @Version
    @Column(name = "version_lock", nullable = false)
    private long versionLock;
}

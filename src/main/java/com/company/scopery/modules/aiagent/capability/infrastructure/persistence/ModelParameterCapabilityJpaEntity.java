package com.company.scopery.modules.aiagent.capability.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentTableNames;
import jakarta.persistence.*;
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
    name = AiAgentTableNames.MODEL_PARAMETER_CAPABILITY,
    uniqueConstraints = @UniqueConstraint(
        name = "uq_aiagent_model_parameter_capability_model_id_parameter_name",
        columnNames = {"model_id", "parameter_name"}
    ),
    indexes = {
        @Index(name = "idx_aiagent_model_parameter_capability_model_id",       columnList = "model_id"),
        @Index(name = "idx_aiagent_model_parameter_capability_parameter_name", columnList = "parameter_name"),
        @Index(name = "idx_aiagent_model_parameter_capability_support_status", columnList = "support_status"),
        @Index(name = "idx_aiagent_model_parameter_capability_value_type",     columnList = "value_type"),
        @Index(name = "idx_aiagent_model_parameter_capability_status",         columnList = "status")
    }
)
public class ModelParameterCapabilityJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "model_id", nullable = false, updatable = false)
    private UUID modelId;

    @Column(name = "parameter_name", nullable = false, length = 100, updatable = false)
    private String parameterName;

    @Column(name = "api_parameter_key", length = 255)
    private String apiParameterKey;

    @Column(name = "support_status", nullable = false, length = 50)
    private String supportStatus;

    @Column(name = "value_type", nullable = false, length = 50)
    private String valueType;

    @Column(name = "min_value", precision = 12, scale = 4)
    private BigDecimal minValue;

    @Column(name = "max_value", precision = 12, scale = 4)
    private BigDecimal maxValue;

    @Column(name = "default_value", length = 500)
    private String defaultValue;

    @Column(name = "nullable", nullable = false)
    private boolean nullable;

    @Column(name = "if_null_behavior", length = 100)
    private String ifNullBehavior;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

}
package com.company.scopery.modules.aiagent.deployment.infrastructure.persistence;

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
    name = AiAgentTableNames.MODEL_DEPLOYMENT,
    uniqueConstraints = @UniqueConstraint(
        name = "uq_aiagent_model_deployment_model_id_code",
        columnNames = {"model_id", "code"}
    ),
    indexes = {
        @Index(name = "idx_aiagent_model_deployment_model_id",              columnList = "model_id"),
        @Index(name = "idx_aiagent_model_deployment_environment",           columnList = "environment"),
        @Index(name = "idx_aiagent_model_deployment_status",               columnList = "status"),
        @Index(name = "idx_aiagent_model_deployment_code",                 columnList = "code"),
        @Index(name = "idx_aiagent_model_deployment_provider_deployment_id", columnList = "provider_deployment_id"),
        @Index(name = "idx_aiagent_model_deployment_is_default",           columnList = "is_default")
    }
)
public class ModelDeploymentJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "model_id", nullable = false, updatable = false)
    private UUID modelId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "code", nullable = false, length = 100, updatable = false)
    private String code;

    @Column(name = "environment", nullable = false, length = 50, updatable = false)
    private String environment;

    @Column(name = "provider_deployment_id", nullable = false, length = 255)
    private String providerDeploymentId;

    @Column(name = "endpoint_url", length = 500)
    private String endpointUrl;

    @Column(name = "default_temperature", precision = 4, scale = 2)
    private BigDecimal defaultTemperature;

    @Column(name = "default_max_output_tokens")
    private Integer defaultMaxOutputTokens;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}

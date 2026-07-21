package com.company.scopery.modules.aiassistant.workspaceconfig.infrastructure.persistence;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiassistant.shared.constant.AiAssistantTableNames;
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
    name = AiAssistantTableNames.WORKSPACE_CONFIG,
    indexes = {
        @Index(name = "idx_aiassistant_workspace_config_workspace", columnList = "workspace_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_aiassistant_workspace_config_workspace", columnNames = "workspace_id")
    }
)
public class AiAssistantWorkspaceConfigJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "workspace_id", nullable = false, updatable = false)
    private UUID workspaceId;

    @Column(name = "model_deployment_id")
    private UUID modelDeploymentId;

    @Column(name = "model_provider", length = 100)
    private String modelProvider;

    @Column(name = "model_name", length = 200)
    private String modelName;

    @Column(name = "system_prompt_override", columnDefinition = "text")
    private String systemPromptOverride;

    @Column(name = "temperature_override", precision = 4, scale = 2)
    private BigDecimal temperatureOverride;

    @Column(name = "max_output_tokens_override")
    private Integer maxOutputTokensOverride;
}

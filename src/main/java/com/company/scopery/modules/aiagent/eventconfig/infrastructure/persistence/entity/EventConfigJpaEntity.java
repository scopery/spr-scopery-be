package com.company.scopery.modules.aiagent.eventconfig.infrastructure.persistence.entity;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = AiAgentTableNames.EVENT_CONFIG,
    uniqueConstraints = @UniqueConstraint(name = "uq_aiagent_event_config_code", columnNames = {"code"}),
    indexes = {
        @Index(name = "idx_aiagent_event_config_code",                  columnList = "code"),
        @Index(name = "idx_aiagent_event_config_event_definition_id",   columnList = "event_definition_id"),
        @Index(name = "idx_aiagent_event_config_environment",           columnList = "environment"),
        @Index(name = "idx_aiagent_event_config_trigger_type",          columnList = "trigger_type"),
        @Index(name = "idx_aiagent_event_config_status",                columnList = "status"),
        @Index(name = "idx_aiagent_event_config_agent_id",              columnList = "agent_id"),
        @Index(name = "idx_aiagent_event_config_prompt_version_id",     columnList = "prompt_version_id"),
        @Index(name = "idx_aiagent_event_config_model_deployment_id",   columnList = "model_deployment_id")
    }
)
public class EventConfigJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100, updatable = false)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "event_definition_id", nullable = false, updatable = false)
    private UUID eventDefinitionId;

    @Column(name = "environment", nullable = false, length = 50, updatable = false)
    private String environment;

    @Column(name = "trigger_type", nullable = false, length = 50)
    private String triggerType;

    @Column(name = "agent_id", nullable = false)
    private UUID agentId;

    @Column(name = "prompt_version_id", nullable = false)
    private UUID promptVersionId;

    @Column(name = "model_deployment_id", nullable = false)
    private UUID modelDeploymentId;

    @Column(name = "condition_expression", columnDefinition = "TEXT")
    private String conditionExpression;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "input_mapping_json", columnDefinition = "TEXT")
    private String inputMappingJson;

    @Column(name = "output_mapping_json", columnDefinition = "TEXT")
    private String outputMappingJson;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "activated_by", length = 100)
    private String activatedBy;

    @Column(name = "deactivated_at")
    private Instant deactivatedAt;

    @Column(name = "deactivated_by", length = 100)
    private String deactivatedBy;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

}

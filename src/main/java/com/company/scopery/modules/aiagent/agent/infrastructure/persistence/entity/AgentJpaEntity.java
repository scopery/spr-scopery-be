package com.company.scopery.modules.aiagent.agent.infrastructure.persistence.entity;

import com.company.scopery.common.audit.AuditableJpaEntity;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentTableNames;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
    name = AiAgentTableNames.AGENT,
    uniqueConstraints = @UniqueConstraint(name = "uq_aiagent_agent_code", columnNames = {"code"}),
    indexes = {
        @Index(name = "idx_aiagent_agent_code",                         columnList = "code"),
        @Index(name = "idx_aiagent_agent_type",                         columnList = "type"),
        @Index(name = "idx_aiagent_agent_status",                       columnList = "status"),
        @Index(name = "idx_aiagent_agent_output_format",                columnList = "output_format"),
        @Index(name = "idx_aiagent_agent_default_model_deployment_id",  columnList = "default_model_deployment_id")
    }
)
public class AgentJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "code", nullable = false, length = 100, updatable = false)
    private String code;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "default_model_deployment_id")
    private UUID defaultModelDeploymentId;

    @Column(name = "output_format", length = 50)
    private String outputFormat;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}

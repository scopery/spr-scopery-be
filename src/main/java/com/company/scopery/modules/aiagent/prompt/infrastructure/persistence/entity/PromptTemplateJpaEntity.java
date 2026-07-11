package com.company.scopery.modules.aiagent.prompt.infrastructure.persistence.entity;

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
    name = AiAgentTableNames.PROMPT_TEMPLATE,
    uniqueConstraints = @UniqueConstraint(
        name = "uq_aiagent_prompt_template_agent_id_code",
        columnNames = {"agent_id", "code"}
    ),
    indexes = {
        @Index(name = "idx_aiagent_prompt_template_agent_id", columnList = "agent_id"),
        @Index(name = "idx_aiagent_prompt_template_code",     columnList = "code"),
        @Index(name = "idx_aiagent_prompt_template_status",   columnList = "status")
    }
)
public class PromptTemplateJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "agent_id", nullable = false, updatable = false)
    private UUID agentId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "code", nullable = false, length = 100, updatable = false)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

}

package com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity;

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
    name = AiAgentTableNames.AGENT_TOOL_BINDING,
    uniqueConstraints = @UniqueConstraint(
        name = "uq_aiagent_agent_tool_binding_agent_tool",
        columnNames = {"agent_id", "tool_id"}
    ),
    indexes = {
        @Index(name = "idx_aiagent_agent_tool_binding_agent_id", columnList = "agent_id"),
        @Index(name = "idx_aiagent_agent_tool_binding_tool_id", columnList = "tool_id"),
        @Index(name = "idx_aiagent_agent_tool_binding_status", columnList = "status")
    }
)
public class AiAgentToolBindingJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "agent_id", nullable = false, updatable = false)
    private UUID agentId;

    @Column(name = "tool_id", nullable = false, updatable = false)
    private UUID toolId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}

package com.company.scopery.modules.aiagent.tool.infrastructure.persistence.entity;

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
    name = AiAgentTableNames.TOOL_EXECUTION,
    indexes = {
        @Index(name = "idx_aiagent_tool_execution_tool_id", columnList = "tool_id"),
        @Index(name = "idx_aiagent_tool_execution_agent_id", columnList = "agent_id"),
        @Index(name = "idx_aiagent_tool_execution_status", columnList = "status"),
        @Index(name = "idx_aiagent_tool_execution_created_at", columnList = "created_at")
    }
)
public class AiToolExecutionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tool_id", nullable = false, updatable = false)
    private UUID toolId;

    @Column(name = "agent_id")
    private UUID agentId;

    @Column(name = "requested_by_user_id")
    private UUID requestedByUserId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "approval_state", nullable = false, length = 50)
    private String approvalState;

    @Column(name = "input_summary", length = 1000)
    private String inputSummary;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "result_summary", length = 2000)
    private String resultSummary;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;
}

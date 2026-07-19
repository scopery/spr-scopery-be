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
    name = AiAgentTableNames.TOOL,
    uniqueConstraints = @UniqueConstraint(name = "uq_aiagent_tool_code", columnNames = "code"),
    indexes = {
        @Index(name = "idx_aiagent_tool_code", columnList = "code"),
        @Index(name = "idx_aiagent_tool_category", columnList = "category"),
        @Index(name = "idx_aiagent_tool_status", columnList = "status")
    }
)
public class AiToolJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 100, updatable = false)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false, length = 100)
    private String category;

    @Column(name = "mutation_type", nullable = false, length = 50)
    private String mutationType;

    @Column(name = "requires_human_approval", nullable = false)
    private boolean requiresHumanApproval;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}

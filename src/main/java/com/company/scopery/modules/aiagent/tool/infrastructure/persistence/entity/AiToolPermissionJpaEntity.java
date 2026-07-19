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
    name = AiAgentTableNames.TOOL_PERMISSION,
    uniqueConstraints = @UniqueConstraint(
        name = "uq_aiagent_tool_permission_tool_code",
        columnNames = {"tool_id", "permission_code"}
    ),
    indexes = {
        @Index(name = "idx_aiagent_tool_permission_tool_id", columnList = "tool_id"),
        @Index(name = "idx_aiagent_tool_permission_permission_code", columnList = "permission_code")
    }
)
public class AiToolPermissionJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tool_id", nullable = false, updatable = false)
    private UUID toolId;

    @Column(name = "permission_code", nullable = false, length = 150, updatable = false)
    private String permissionCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}

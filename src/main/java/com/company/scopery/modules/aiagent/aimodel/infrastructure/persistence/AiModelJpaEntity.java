package com.company.scopery.modules.aiagent.aimodel.infrastructure.persistence;

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
    name = AiAgentTableNames.AI_MODEL,
    uniqueConstraints = @UniqueConstraint(
        name = "uq_aiagent_model_provider_id_code",
        columnNames = {"provider_id", "code"}
    ),
    indexes = {
        @Index(name = "idx_aiagent_model_provider_id",      columnList = "provider_id"),
        @Index(name = "idx_aiagent_model_status",           columnList = "status"),
        @Index(name = "idx_aiagent_model_type",             columnList = "type"),
        @Index(name = "idx_aiagent_model_code",             columnList = "code"),
        @Index(name = "idx_aiagent_model_provider_model_id", columnList = "provider_model_id")
    }
)
public class AiModelJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "provider_id", nullable = false, updatable = false)
    private UUID providerId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "code", nullable = false, length = 100, updatable = false)
    private String code;

    @Column(name = "provider_model_id", nullable = false, length = 255)
    private String providerModelId;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}

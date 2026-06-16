package com.company.scopery.modules.aiagent.provider.infrastructure.persistence;

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
    name = AiAgentTableNames.PROVIDER,
    uniqueConstraints = @UniqueConstraint(name = "uq_aiagent_provider_code", columnNames = "code"),
    indexes = {
        @Index(name = "idx_aiagent_provider_status", columnList = "status"),
        @Index(name = "idx_aiagent_provider_type",   columnList = "type"),
        @Index(name = "idx_aiagent_provider_code",   columnList = "code")
    }
)
public class ProviderJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @Column(name = "api_base_url", length = 500)
    private String apiBaseUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
}

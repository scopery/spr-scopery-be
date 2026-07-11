package com.company.scopery.modules.aiagent.providersecret.infrastructure.persistence.entity;

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
        name = AiAgentTableNames.PROVIDER_SECRET,
        indexes = {
                @Index(name = "idx_aiagent_provider_secret_provider_id", columnList = "provider_id"),
                @Index(name = "idx_aiagent_provider_secret_secret_type", columnList = "secret_type"),
                @Index(name = "idx_aiagent_provider_secret_status",      columnList = "status")
        }
)
public class ProviderSecretJpaEntity extends AuditableJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "provider_id", nullable = false, updatable = false)
    private UUID providerId;

    @Column(name = "secret_type", nullable = false, length = 50)
    private String secretType;

    @Column(name = "encrypted_value", nullable = false, columnDefinition = "TEXT")
    private String encryptedValue;

    @Column(name = "iv", nullable = false, length = 255)
    private String iv;

    @Column(name = "key_version", nullable = false, length = 50)
    private String keyVersion;

    @Column(name = "masked_value", nullable = false, length = 100)
    private String maskedValue;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "last_rotated_at")
    private Instant lastRotatedAt;

}

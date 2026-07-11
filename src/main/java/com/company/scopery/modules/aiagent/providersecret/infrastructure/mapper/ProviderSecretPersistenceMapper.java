package com.company.scopery.modules.aiagent.providersecret.infrastructure.mapper;

import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretStatus;
import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretType;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecret;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.persistence.entity.ProviderSecretJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProviderSecretPersistenceMapper {

    public ProviderSecretJpaEntity toJpaEntity(ProviderSecret secret) {
        ProviderSecretJpaEntity entity = new ProviderSecretJpaEntity();
        entity.setId(secret.id());
        entity.setProviderId(secret.providerId());
        entity.setSecretType(secret.secretType().name());
        entity.setEncryptedValue(secret.encryptedValue());
        entity.setIv(secret.iv());
        entity.setKeyVersion(secret.keyVersion());
        entity.setMaskedValue(secret.maskedValue());
        entity.setDescription(secret.description());
        entity.setStatus(secret.status().name());
        entity.setLastRotatedAt(secret.lastRotatedAt());
        if (secret.createdAt() != null) {
            entity.setCreatedAt(secret.createdAt());
        }
        return entity;
    }

    public ProviderSecret toDomain(ProviderSecretJpaEntity entity) {
        return ProviderSecret.reconstitute(
                entity.getId(),
                entity.getProviderId(),
                ProviderSecretType.valueOf(entity.getSecretType()),
                entity.getEncryptedValue(),
                entity.getIv(),
                entity.getKeyVersion(),
                entity.getMaskedValue(),
                entity.getDescription(),
                ProviderSecretStatus.valueOf(entity.getStatus()),
                entity.getLastRotatedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

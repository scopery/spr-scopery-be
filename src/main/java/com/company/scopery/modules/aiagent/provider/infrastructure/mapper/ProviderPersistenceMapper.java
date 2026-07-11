package com.company.scopery.modules.aiagent.provider.infrastructure.mapper;

import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderType;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.valueobject.ProviderCode;
import com.company.scopery.modules.aiagent.provider.infrastructure.persistence.entity.ProviderJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProviderPersistenceMapper {

    public ProviderJpaEntity toJpaEntity(Provider provider) {
        ProviderJpaEntity entity = new ProviderJpaEntity();
        entity.setId(provider.id());
        entity.setName(provider.name());
        entity.setCode(provider.code().value());
        entity.setType(provider.type().name());
        entity.setApiBaseUrl(provider.apiBaseUrl());
        entity.setDescription(provider.description());
        entity.setStatus(provider.status().name());
        // Set createdAt so Spring Data's isNew() check (via @CreatedDate null-detection) returns false,
        // ensuring merge() is used for both INSERT and UPDATE consistently.
        if (provider.createdAt() != null) {
            entity.setCreatedAt(provider.createdAt());
        }
        return entity;
    }

    public Provider toDomain(ProviderJpaEntity entity) {
        return Provider.reconstitute(
                entity.getId(),
                entity.getName(),
                ProviderCode.of(entity.getCode()),
                ProviderType.valueOf(entity.getType()),
                entity.getApiBaseUrl(),
                entity.getDescription(),
                ProviderStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

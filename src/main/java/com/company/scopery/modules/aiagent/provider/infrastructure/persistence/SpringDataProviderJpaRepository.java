package com.company.scopery.modules.aiagent.provider.infrastructure.persistence;

import com.company.scopery.modules.aiagent.provider.infrastructure.persistence.entity.ProviderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataProviderJpaRepository
        extends JpaRepository<ProviderJpaEntity, UUID>, JpaSpecificationExecutor<ProviderJpaEntity> {

    boolean existsByCode(String code);
}

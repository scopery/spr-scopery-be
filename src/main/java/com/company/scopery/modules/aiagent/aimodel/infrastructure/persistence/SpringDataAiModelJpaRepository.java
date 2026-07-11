package com.company.scopery.modules.aiagent.aimodel.infrastructure.persistence;

import com.company.scopery.modules.aiagent.aimodel.infrastructure.persistence.entity.AiModelJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataAiModelJpaRepository
        extends JpaRepository<AiModelJpaEntity, UUID>, JpaSpecificationExecutor<AiModelJpaEntity> {

    boolean existsByProviderIdAndCode(UUID providerId, String code);
}

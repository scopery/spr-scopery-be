package com.company.scopery.modules.aiagent.capability.infrastructure.persistence;

import com.company.scopery.modules.aiagent.capability.infrastructure.persistence.entity.ModelParameterCapabilityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SpringDataModelParameterCapabilityJpaRepository
        extends JpaRepository<ModelParameterCapabilityJpaEntity, UUID>,
                JpaSpecificationExecutor<ModelParameterCapabilityJpaEntity> {

    boolean existsByModelIdAndParameterName(UUID modelId, String parameterName);
}

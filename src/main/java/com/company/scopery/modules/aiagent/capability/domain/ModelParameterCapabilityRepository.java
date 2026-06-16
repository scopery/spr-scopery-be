package com.company.scopery.modules.aiagent.capability.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ModelParameterCapabilityRepository {

    ModelParameterCapability save(ModelParameterCapability capability);

    Optional<ModelParameterCapability> findById(UUID id);

    boolean existsByModelIdAndParameterName(UUID modelId, ModelParameterName parameterName);

    Page<ModelParameterCapability> findAll(UUID modelId, String parameterName,
                                           ModelParameterSupportStatus supportStatus,
                                           ModelParameterValueType valueType,
                                           ModelParameterCapabilityStatus status,
                                           Pageable pageable);
}

package com.company.scopery.modules.aiagent.capability.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterCapabilityStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterSupportStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterValueType;
import com.company.scopery.modules.aiagent.capability.domain.valueobject.ModelParameterName;

import java.util.Optional;
import java.util.UUID;

public interface ModelParameterCapabilityRepository {

    ModelParameterCapability save(ModelParameterCapability capability);

    Optional<ModelParameterCapability> findById(UUID id);

    boolean existsByModelIdAndParameterName(UUID modelId, ModelParameterName parameterName);

    PageResult<ModelParameterCapability> findAll(UUID modelId, String parameterName,
                                                  ModelParameterSupportStatus supportStatus,
                                                  ModelParameterValueType valueType,
                                                  ModelParameterCapabilityStatus status,
                                                  PageQuery pageQuery);
}

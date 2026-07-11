package com.company.scopery.modules.aiagent.aimodel.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelType;
import com.company.scopery.modules.aiagent.aimodel.domain.valueobject.AiModelCode;

import java.util.Optional;
import java.util.UUID;

public interface AiModelRepository {

    AiModel save(AiModel model);

    Optional<AiModel> findById(UUID id);

    boolean existsByProviderIdAndCode(UUID providerId, AiModelCode code);

    PageResult<AiModel> findAll(UUID providerId, String keyword, AiModelStatus status, AiModelType type, PageQuery pageQuery);
}

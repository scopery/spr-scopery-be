package com.company.scopery.modules.aiagent.capability.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.capability.application.query.GetModelParameterCapabilityDetailQuery;
import com.company.scopery.modules.aiagent.capability.application.query.SearchModelParameterCapabilityQuery;
import com.company.scopery.modules.aiagent.capability.application.response.ModelParameterCapabilityDetailResponse;
import com.company.scopery.modules.aiagent.capability.application.response.ModelParameterCapabilityResponse;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterCapabilityStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterSupportStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterValueType;
import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapability;
import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapabilityRepository;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ModelParameterCapabilityQueryService {

    private final ModelParameterCapabilityRepository capabilityRepository;
    private final AiModelRepository aiModelRepository;

    public ModelParameterCapabilityQueryService(ModelParameterCapabilityRepository capabilityRepository,
                                                AiModelRepository aiModelRepository) {
        this.capabilityRepository = capabilityRepository;
        this.aiModelRepository = aiModelRepository;
    }

    @Transactional(readOnly = true)
    public ModelParameterCapabilityDetailResponse getModelParameterCapabilityDetail(
            GetModelParameterCapabilityDetailQuery query) {
        ModelParameterCapability capability = findOrThrow(query.id());
        String modelName = loadModelName(capability.modelId());
        return ModelParameterCapabilityDetailResponse.from(capability, modelName);
    }

    @Transactional(readOnly = true)
    public PageResult<ModelParameterCapabilityResponse> searchModelParameterCapabilities(
            SearchModelParameterCapabilityQuery query) {
        ModelParameterSupportStatus supportStatus = AiAgentEnumParser.parseOptional(
                ModelParameterSupportStatus.class, query.supportStatus(),
                AiAgentErrorCatalog.INVALID_MODEL_PARAMETER_SUPPORT_STATUS.code(), "supportStatus");
        ModelParameterValueType valueType = AiAgentEnumParser.parseOptional(
                ModelParameterValueType.class, query.valueType(),
                AiAgentErrorCatalog.INVALID_MODEL_PARAMETER_VALUE_TYPE.code(), "valueType");
        ModelParameterCapabilityStatus status = AiAgentEnumParser.parseOptional(
                ModelParameterCapabilityStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_MODEL_PARAMETER_CAPABILITY_STATUS.code(), "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);

        return capabilityRepository
                .findAll(query.modelId(), query.parameterName(), supportStatus, valueType, status, pageQuery)
                .map(ModelParameterCapabilityResponse::from);
    }

    private ModelParameterCapability findOrThrow(UUID id) {
        return capabilityRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.modelParameterCapabilityNotFound(id));
    }

    private String loadModelName(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .map(AiModel::name)
                .orElse(null);
    }
}

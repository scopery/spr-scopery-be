package com.company.scopery.modules.aiagent.capability.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.AiModelStatus;
import com.company.scopery.modules.aiagent.capability.application.command.*;
import com.company.scopery.modules.aiagent.capability.application.query.*;
import com.company.scopery.modules.aiagent.capability.application.response.*;
import com.company.scopery.modules.aiagent.capability.domain.*;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ModelParameterCapabilityApplicationService {

    private final ModelParameterCapabilityRepository capabilityRepository;
    private final AiModelRepository aiModelRepository;
    private final AiAgentActivityLogger activityLogger;

    public ModelParameterCapabilityApplicationService(
            ModelParameterCapabilityRepository capabilityRepository,
            AiModelRepository aiModelRepository,
            AiAgentActivityLogger activityLogger) {
        this.capabilityRepository = capabilityRepository;
        this.aiModelRepository = aiModelRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ModelParameterCapabilityResponse createModelParameterCapability(
            CreateModelParameterCapabilityCommand command) {

        AiModel model = findModelOrThrow(command.modelId());

        if (model.status() == AiModelStatus.DEPRECATED) {
            throw AiAgentExceptions.modelParameterCapabilityModelDeprecated(model.code().value());
        }

        ModelParameterName parameterName = ModelParameterName.of(command.parameterName());

        if (capabilityRepository.existsByModelIdAndParameterName(command.modelId(), parameterName)) {
            throw AiAgentExceptions.modelParameterCapabilityAlreadyExists(parameterName.value());
        }

        ModelParameterSupportStatus supportStatus = AiAgentEnumParser.parseRequired(
                ModelParameterSupportStatus.class, command.supportStatus(),
                AiAgentErrorCatalog.INVALID_MODEL_PARAMETER_SUPPORT_STATUS.code(), "supportStatus");
        ModelParameterValueType valueType = AiAgentEnumParser.parseRequired(
                ModelParameterValueType.class, command.valueType(),
                AiAgentErrorCatalog.INVALID_MODEL_PARAMETER_VALUE_TYPE.code(), "valueType");

        ModelParameterIfNullBehavior ifNullBehavior = AiAgentEnumParser.parseOptional(
                ModelParameterIfNullBehavior.class, command.ifNullBehavior(),
                AiAgentErrorCatalog.INVALID_MODEL_PARAMETER_IF_NULL_BEHAVIOR.code(), "ifNullBehavior");

        ModelParameterCapability capability = ModelParameterCapability.create(
                command.modelId(), parameterName, command.apiParameterKey(),
                supportStatus, valueType, command.minValue(), command.maxValue(),
                command.defaultValue(), command.nullable(), ifNullBehavior,
                command.description());

        ModelParameterCapability saved = capabilityRepository.save(capability);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_PARAMETER_CAPABILITY, saved.id(),
                AiAgentActivityActions.CREATE_MODEL_PARAMETER_CAPABILITY,
                "Model parameter capability created: " + saved.parameterName().value()
                        + " for model " + model.code().value());

        return ModelParameterCapabilityResponse.from(saved);
    }

    @Transactional
    public ModelParameterCapabilityDetailResponse updateModelParameterCapability(
            UpdateModelParameterCapabilityCommand command) {

        ModelParameterCapability capability = findOrThrow(command.id());

        ModelParameterSupportStatus supportStatus = AiAgentEnumParser.parseRequired(
                ModelParameterSupportStatus.class, command.supportStatus(),
                AiAgentErrorCatalog.INVALID_MODEL_PARAMETER_SUPPORT_STATUS.code(), "supportStatus");
        ModelParameterValueType valueType = AiAgentEnumParser.parseRequired(
                ModelParameterValueType.class, command.valueType(),
                AiAgentErrorCatalog.INVALID_MODEL_PARAMETER_VALUE_TYPE.code(), "valueType");

        ModelParameterIfNullBehavior ifNullBehavior = AiAgentEnumParser.parseOptional(
                ModelParameterIfNullBehavior.class, command.ifNullBehavior(),
                AiAgentErrorCatalog.INVALID_MODEL_PARAMETER_IF_NULL_BEHAVIOR.code(), "ifNullBehavior");

        capability.update(command.apiParameterKey(), supportStatus, valueType,
                command.minValue(), command.maxValue(), command.defaultValue(),
                command.nullable(), ifNullBehavior, command.description());

        ModelParameterCapability saved = capabilityRepository.save(capability);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_PARAMETER_CAPABILITY, saved.id(),
                AiAgentActivityActions.UPDATE_MODEL_PARAMETER_CAPABILITY,
                "Model parameter capability updated: " + saved.parameterName().value());

        String modelName = loadModelName(saved.modelId());
        return ModelParameterCapabilityDetailResponse.from(saved, modelName);
    }

    @Transactional(readOnly = true)
    public ModelParameterCapabilityDetailResponse getModelParameterCapabilityDetail(
            GetModelParameterCapabilityDetailQuery query) {

        ModelParameterCapability capability = findOrThrow(query.id());
        String modelName = loadModelName(capability.modelId());
        return ModelParameterCapabilityDetailResponse.from(capability, modelName);
    }

    @Transactional(readOnly = true)
    public Page<ModelParameterCapabilityResponse> searchModelParameterCapabilities(
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

        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, AiAgentSortFields.CREATED_AT));

        return capabilityRepository
                .findAll(query.modelId(), query.parameterName(), supportStatus, valueType, status, pageable)
                .map(ModelParameterCapabilityResponse::from);
    }

    @Transactional
    public ModelParameterCapabilityDetailResponse activateModelParameterCapability(
            ActivateModelParameterCapabilityCommand command) {

        ModelParameterCapability capability = findOrThrow(command.id());
        AiModel model = findModelOrThrow(capability.modelId());

        if (model.status() == AiModelStatus.DEPRECATED) {
            throw AiAgentExceptions.modelParameterCapabilityModelDeprecated(model.code().value());
        }

        capability.activate();
        ModelParameterCapability saved = capabilityRepository.save(capability);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_PARAMETER_CAPABILITY, saved.id(),
                AiAgentActivityActions.ACTIVATE_MODEL_PARAMETER_CAPABILITY,
                "Model parameter capability activated: " + saved.parameterName().value());

        return ModelParameterCapabilityDetailResponse.from(saved, model.name());
    }

    @Transactional
    public ModelParameterCapabilityDetailResponse deactivateModelParameterCapability(
            DeactivateModelParameterCapabilityCommand command) {

        ModelParameterCapability capability = findOrThrow(command.id());
        capability.deactivate();
        ModelParameterCapability saved = capabilityRepository.save(capability);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_PARAMETER_CAPABILITY, saved.id(),
                AiAgentActivityActions.DEACTIVATE_MODEL_PARAMETER_CAPABILITY,
                "Model parameter capability deactivated: " + saved.parameterName().value());

        String modelName = loadModelName(saved.modelId());
        return ModelParameterCapabilityDetailResponse.from(saved, modelName);
    }

    private ModelParameterCapability findOrThrow(UUID id) {
        return capabilityRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.modelParameterCapabilityNotFound(id));
    }

    private AiModel findModelOrThrow(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .orElseThrow(() -> AiAgentExceptions.modelParameterCapabilityModelNotFound(modelId));
    }

    private String loadModelName(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .map(AiModel::name)
                .orElse(null);
    }
}
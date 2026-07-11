package com.company.scopery.modules.aiagent.capability.application.action;

import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.capability.application.command.UpdateModelParameterCapabilityCommand;
import com.company.scopery.modules.aiagent.capability.application.response.ModelParameterCapabilityDetailResponse;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterIfNullBehavior;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterSupportStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterValueType;
import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapability;
import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapabilityRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateModelParameterCapabilityAction {

    private final ModelParameterCapabilityRepository capabilityRepository;
    private final AiModelRepository aiModelRepository;
    private final AiAgentActivityLogger activityLogger;

    public UpdateModelParameterCapabilityAction(ModelParameterCapabilityRepository capabilityRepository,
                                                AiModelRepository aiModelRepository,
                                                AiAgentActivityLogger activityLogger) {
        this.capabilityRepository = capabilityRepository;
        this.aiModelRepository = aiModelRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ModelParameterCapabilityDetailResponse execute(UpdateModelParameterCapabilityCommand command) {
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

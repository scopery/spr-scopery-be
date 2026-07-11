package com.company.scopery.modules.aiagent.capability.application.action;

import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.capability.application.command.CreateModelParameterCapabilityCommand;
import com.company.scopery.modules.aiagent.capability.application.response.ModelParameterCapabilityResponse;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterIfNullBehavior;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterSupportStatus;
import com.company.scopery.modules.aiagent.capability.domain.enums.ModelParameterValueType;
import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapability;
import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapabilityRepository;
import com.company.scopery.modules.aiagent.capability.domain.valueobject.ModelParameterName;
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
public class CreateModelParameterCapabilityAction {

    private final ModelParameterCapabilityRepository capabilityRepository;
    private final AiModelRepository aiModelRepository;
    private final AiAgentActivityLogger activityLogger;

    public CreateModelParameterCapabilityAction(ModelParameterCapabilityRepository capabilityRepository,
                                                AiModelRepository aiModelRepository,
                                                AiAgentActivityLogger activityLogger) {
        this.capabilityRepository = capabilityRepository;
        this.aiModelRepository = aiModelRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ModelParameterCapabilityResponse execute(CreateModelParameterCapabilityCommand command) {
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

    private AiModel findModelOrThrow(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .orElseThrow(() -> AiAgentExceptions.modelParameterCapabilityModelNotFound(modelId));
    }
}

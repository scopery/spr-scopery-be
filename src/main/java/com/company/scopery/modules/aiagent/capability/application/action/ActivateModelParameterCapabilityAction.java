package com.company.scopery.modules.aiagent.capability.application.action;

import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.capability.application.command.ActivateModelParameterCapabilityCommand;
import com.company.scopery.modules.aiagent.capability.application.response.ModelParameterCapabilityDetailResponse;
import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapability;
import com.company.scopery.modules.aiagent.capability.domain.model.ModelParameterCapabilityRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ActivateModelParameterCapabilityAction {

    private final ModelParameterCapabilityRepository capabilityRepository;
    private final AiModelRepository aiModelRepository;
    private final AiAgentActivityLogger activityLogger;

    public ActivateModelParameterCapabilityAction(ModelParameterCapabilityRepository capabilityRepository,
                                                  AiModelRepository aiModelRepository,
                                                  AiAgentActivityLogger activityLogger) {
        this.capabilityRepository = capabilityRepository;
        this.aiModelRepository = aiModelRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ModelParameterCapabilityDetailResponse execute(ActivateModelParameterCapabilityCommand command) {
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

    private ModelParameterCapability findOrThrow(UUID id) {
        return capabilityRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.modelParameterCapabilityNotFound(id));
    }

    private AiModel findModelOrThrow(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .orElseThrow(() -> AiAgentExceptions.modelParameterCapabilityModelNotFound(modelId));
    }
}

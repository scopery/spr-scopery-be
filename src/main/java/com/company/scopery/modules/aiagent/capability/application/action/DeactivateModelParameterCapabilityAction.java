package com.company.scopery.modules.aiagent.capability.application.action;

import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.capability.application.command.DeactivateModelParameterCapabilityCommand;
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
public class DeactivateModelParameterCapabilityAction {

    private final ModelParameterCapabilityRepository capabilityRepository;
    private final AiModelRepository aiModelRepository;
    private final AiAgentActivityLogger activityLogger;

    public DeactivateModelParameterCapabilityAction(ModelParameterCapabilityRepository capabilityRepository,
                                                    AiModelRepository aiModelRepository,
                                                    AiAgentActivityLogger activityLogger) {
        this.capabilityRepository = capabilityRepository;
        this.aiModelRepository = aiModelRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ModelParameterCapabilityDetailResponse execute(DeactivateModelParameterCapabilityCommand command) {
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

    private String loadModelName(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .map(AiModel::name)
                .orElse(null);
    }
}

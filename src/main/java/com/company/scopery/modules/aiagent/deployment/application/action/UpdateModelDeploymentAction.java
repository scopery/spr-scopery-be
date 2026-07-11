package com.company.scopery.modules.aiagent.deployment.application.action;

import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.deployment.application.command.UpdateModelDeploymentCommand;
import com.company.scopery.modules.aiagent.deployment.application.response.ModelDeploymentDetailResponse;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateModelDeploymentAction {

    private final ModelDeploymentRepository deploymentRepository;
    private final AiModelRepository aiModelRepository;
    private final AiAgentActivityLogger activityLogger;

    public UpdateModelDeploymentAction(ModelDeploymentRepository deploymentRepository,
                                       AiModelRepository aiModelRepository,
                                       AiAgentActivityLogger activityLogger) {
        this.deploymentRepository = deploymentRepository;
        this.aiModelRepository = aiModelRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ModelDeploymentDetailResponse execute(UpdateModelDeploymentCommand command) {
        ModelDeployment deployment = findOrThrow(command.id());

        deployment.update(command.name(), command.providerDeploymentId(), command.endpointUrl(),
                command.defaultTemperature(), command.defaultMaxOutputTokens(),
                command.isDefault(), command.description());

        if (command.isDefault()) {
            deploymentRepository.clearDefaultFlags(deployment.modelId(), deployment.environment(), deployment.id());
        }

        ModelDeployment saved = deploymentRepository.save(deployment);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_DEPLOYMENT, saved.id(),
                AiAgentActivityActions.UPDATE_MODEL_DEPLOYMENT,
                "Model deployment updated: " + saved.code().value());

        String modelName = loadModelName(saved.modelId());
        return ModelDeploymentDetailResponse.from(saved, modelName);
    }

    private ModelDeployment findOrThrow(UUID id) {
        return deploymentRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.modelDeploymentNotFound(id));
    }

    private String loadModelName(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .map(AiModel::name)
                .orElse(null);
    }
}

package com.company.scopery.modules.aiagent.deployment.application.action;

import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.deployment.application.command.ActivateModelDeploymentCommand;
import com.company.scopery.modules.aiagent.deployment.application.response.ModelDeploymentDetailResponse;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
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
public class ActivateModelDeploymentAction {

    private final ModelDeploymentRepository deploymentRepository;
    private final AiModelRepository aiModelRepository;
    private final AiAgentActivityLogger activityLogger;

    public ActivateModelDeploymentAction(ModelDeploymentRepository deploymentRepository,
                                         AiModelRepository aiModelRepository,
                                         AiAgentActivityLogger activityLogger) {
        this.deploymentRepository = deploymentRepository;
        this.aiModelRepository = aiModelRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ModelDeploymentDetailResponse execute(ActivateModelDeploymentCommand command) {
        ModelDeployment deployment = findOrThrow(command.id());

        if (deployment.status() == ModelDeploymentStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedModelDeploymentCannotBeActivated(deployment.code().value());
        }

        AiModel model = findModelOrThrow(deployment.modelId());
        if (model.status() != AiModelStatus.ACTIVE) {
            throw AiAgentExceptions.modelDeploymentModelNotActive(model.code().value());
        }

        deployment.activate();
        ModelDeployment saved = deploymentRepository.save(deployment);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_DEPLOYMENT, saved.id(),
                AiAgentActivityActions.ACTIVATE_MODEL_DEPLOYMENT,
                "Model deployment activated: " + saved.code().value());

        return ModelDeploymentDetailResponse.from(saved, model.name());
    }

    private ModelDeployment findOrThrow(UUID id) {
        return deploymentRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.modelDeploymentNotFound(id));
    }

    private AiModel findModelOrThrow(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .orElseThrow(() -> AiAgentExceptions.modelDeploymentModelNotFound(modelId));
    }
}

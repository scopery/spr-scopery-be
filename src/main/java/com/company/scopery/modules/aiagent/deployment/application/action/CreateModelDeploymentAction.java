package com.company.scopery.modules.aiagent.deployment.application.action;

import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.deployment.application.command.CreateModelDeploymentCommand;
import com.company.scopery.modules.aiagent.deployment.application.response.ModelDeploymentResponse;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentEnvironment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.valueobject.ModelDeploymentCode;
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
public class CreateModelDeploymentAction {

    private final ModelDeploymentRepository deploymentRepository;
    private final AiModelRepository aiModelRepository;
    private final AiAgentActivityLogger activityLogger;

    public CreateModelDeploymentAction(ModelDeploymentRepository deploymentRepository,
                                       AiModelRepository aiModelRepository,
                                       AiAgentActivityLogger activityLogger) {
        this.deploymentRepository = deploymentRepository;
        this.aiModelRepository = aiModelRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ModelDeploymentResponse execute(CreateModelDeploymentCommand command) {
        AiModel model = findModelOrThrow(command.modelId());

        if (model.status() != AiModelStatus.ACTIVE) {
            throw AiAgentExceptions.modelDeploymentModelNotActive(model.code().value());
        }

        ModelDeploymentEnvironment environment = AiAgentEnumParser.parseRequired(
                ModelDeploymentEnvironment.class, command.environment(),
                AiAgentErrorCatalog.INVALID_MODEL_DEPLOYMENT_ENVIRONMENT.code(), "environment");
        ModelDeploymentCode code = ModelDeploymentCode.of(command.code());

        if (deploymentRepository.existsByModelIdAndCode(command.modelId(), code)) {
            throw AiAgentExceptions.modelDeploymentCodeAlreadyExists(code.value());
        }

        if (command.isDefault()) {
            deploymentRepository.clearDefaultFlags(command.modelId(), environment, null);
        }

        ModelDeployment deployment = ModelDeployment.create(
                command.modelId(), command.name(), code, environment,
                command.providerDeploymentId(), command.endpointUrl(),
                command.defaultTemperature(), command.defaultMaxOutputTokens(),
                command.isDefault(), command.description());

        ModelDeployment saved = deploymentRepository.save(deployment);

        activityLogger.logSuccess(AiAgentEntityTypes.MODEL_DEPLOYMENT, saved.id(),
                AiAgentActivityActions.CREATE_MODEL_DEPLOYMENT,
                "Model deployment created: " + saved.code().value());

        return ModelDeploymentResponse.from(saved);
    }

    private AiModel findModelOrThrow(UUID modelId) {
        return aiModelRepository.findById(modelId)
                .orElseThrow(() -> AiAgentExceptions.modelDeploymentModelNotFound(modelId));
    }
}

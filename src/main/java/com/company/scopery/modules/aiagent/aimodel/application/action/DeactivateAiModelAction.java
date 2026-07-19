package com.company.scopery.modules.aiagent.aimodel.application.action;

import com.company.scopery.modules.aiagent.aimodel.application.command.DeactivateAiModelCommand;
import com.company.scopery.modules.aiagent.aimodel.application.response.AiModelDetailResponse;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeactivateAiModelAction {

    private final AiModelRepository aiModelRepository;
    private final ProviderRepository providerRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;

    public DeactivateAiModelAction(AiModelRepository aiModelRepository,
                                   ProviderRepository providerRepository,
                                   ModelDeploymentRepository modelDeploymentRepository,
                                   AiAgentActivityLogger activityLogger) {
        this.aiModelRepository = aiModelRepository;
        this.providerRepository = providerRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiModelDetailResponse execute(DeactivateAiModelCommand command) {
        AiModel model = aiModelRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.aiModelNotFound(command.id()));

        if (modelDeploymentRepository.existsActiveByModelId(model.id())) {
            throw AiAgentExceptions.aiModelHasActiveDeployments(model.id());
        }

        model.deactivate();
        AiModel saved = aiModelRepository.save(model);

        activityLogger.logSuccess(AiAgentEntityTypes.AI_MODEL, saved.id(),
                AiAgentActivityActions.DEACTIVATE_AI_MODEL,
                "AI model deactivated: " + saved.code().value());

        String providerName = loadProviderName(saved.providerId());
        return AiModelDetailResponse.from(saved, providerName);
    }

    private String loadProviderName(UUID providerId) {
        return providerRepository.findById(providerId)
                .map(Provider::name)
                .orElse(null);
    }
}

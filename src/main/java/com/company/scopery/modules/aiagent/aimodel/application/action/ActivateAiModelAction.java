package com.company.scopery.modules.aiagent.aimodel.application.action;

import com.company.scopery.modules.aiagent.aimodel.application.command.ActivateAiModelCommand;
import com.company.scopery.modules.aiagent.aimodel.application.response.AiModelDetailResponse;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ActivateAiModelAction {

    private final AiModelRepository aiModelRepository;
    private final ProviderRepository providerRepository;
    private final AiAgentActivityLogger activityLogger;

    public ActivateAiModelAction(AiModelRepository aiModelRepository,
                                 ProviderRepository providerRepository,
                                 AiAgentActivityLogger activityLogger) {
        this.aiModelRepository = aiModelRepository;
        this.providerRepository = providerRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiModelDetailResponse execute(ActivateAiModelCommand command) {
        AiModel model = aiModelRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.aiModelNotFound(command.id()));

        if (model.status() == AiModelStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedAiModelCannotBeActivated(model.code().value());
        }

        Provider provider = providerRepository.findById(model.providerId())
                .orElseThrow(() -> AiAgentExceptions.aiModelProviderNotFound(model.providerId()));

        if (provider.status() != ProviderStatus.ACTIVE) {
            throw AiAgentExceptions.aiModelProviderNotActive(provider.code().value());
        }

        model.activate();
        AiModel saved = aiModelRepository.save(model);

        activityLogger.logSuccess(AiAgentEntityTypes.AI_MODEL, saved.id(),
                AiAgentActivityActions.ACTIVATE_AI_MODEL,
                "AI model activated: " + saved.code().value());

        return AiModelDetailResponse.from(saved, provider.name());
    }
}

package com.company.scopery.modules.aiagent.aimodel.application.action;

import com.company.scopery.modules.aiagent.aimodel.application.command.UpdateAiModelCommand;
import com.company.scopery.modules.aiagent.aimodel.application.response.AiModelDetailResponse;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelType;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
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
public class UpdateAiModelAction {

    private final AiModelRepository aiModelRepository;
    private final ProviderRepository providerRepository;
    private final AiAgentActivityLogger activityLogger;

    public UpdateAiModelAction(AiModelRepository aiModelRepository,
                               ProviderRepository providerRepository,
                               AiAgentActivityLogger activityLogger) {
        this.aiModelRepository = aiModelRepository;
        this.providerRepository = providerRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiModelDetailResponse execute(UpdateAiModelCommand command) {
        AiModel model = aiModelRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.aiModelNotFound(command.id()));

        AiModelType type = AiAgentEnumParser.parseRequired(
                AiModelType.class, command.type(),
                AiAgentErrorCatalog.INVALID_AI_MODEL_TYPE.code(), "type");
        model.update(command.name(), command.providerModelId(), type, command.description());

        AiModel saved = aiModelRepository.save(model);

        activityLogger.logSuccess(AiAgentEntityTypes.AI_MODEL, saved.id(),
                AiAgentActivityActions.UPDATE_AI_MODEL,
                "AI model updated: " + saved.code().value());

        String providerName = loadProviderName(saved.providerId());
        return AiModelDetailResponse.from(saved, providerName);
    }

    private String loadProviderName(UUID providerId) {
        return providerRepository.findById(providerId)
                .map(Provider::name)
                .orElse(null);
    }
}

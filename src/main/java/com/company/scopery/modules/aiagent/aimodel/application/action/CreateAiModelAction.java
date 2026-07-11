package com.company.scopery.modules.aiagent.aimodel.application.action;

import com.company.scopery.modules.aiagent.aimodel.application.command.CreateAiModelCommand;
import com.company.scopery.modules.aiagent.aimodel.application.response.AiModelResponse;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelType;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
import com.company.scopery.modules.aiagent.aimodel.domain.valueobject.AiModelCode;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
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
public class CreateAiModelAction {

    private final AiModelRepository aiModelRepository;
    private final ProviderRepository providerRepository;
    private final AiAgentActivityLogger activityLogger;

    public CreateAiModelAction(AiModelRepository aiModelRepository,
                               ProviderRepository providerRepository,
                               AiAgentActivityLogger activityLogger) {
        this.aiModelRepository = aiModelRepository;
        this.providerRepository = providerRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiModelResponse execute(CreateAiModelCommand command) {
        Provider provider = providerRepository.findById(command.providerId())
                .orElseThrow(() -> AiAgentExceptions.aiModelProviderNotFound(command.providerId()));

        if (provider.status() != ProviderStatus.ACTIVE) {
            throw AiAgentExceptions.aiModelProviderNotActive(provider.code().value());
        }

        AiModelCode code = AiModelCode.of(command.code());

        if (aiModelRepository.existsByProviderIdAndCode(command.providerId(), code)) {
            throw AiAgentExceptions.aiModelCodeAlreadyExists(code.value());
        }

        AiModelType type = AiAgentEnumParser.parseRequired(
                AiModelType.class, command.type(),
                AiAgentErrorCatalog.INVALID_AI_MODEL_TYPE.code(), "type");

        AiModel model = AiModel.create(command.providerId(), command.name(), code,
                command.providerModelId(), type, command.description());

        AiModel saved = aiModelRepository.save(model);

        activityLogger.logSuccess(AiAgentEntityTypes.AI_MODEL, saved.id(),
                AiAgentActivityActions.CREATE_AI_MODEL,
                "AI model created: " + saved.code().value());

        return AiModelResponse.from(saved);
    }
}

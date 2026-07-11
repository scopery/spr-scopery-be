package com.company.scopery.modules.aiagent.provider.application.action;

import com.company.scopery.modules.aiagent.provider.application.command.UpdateProviderCommand;
import com.company.scopery.modules.aiagent.provider.application.response.ProviderDetailResponse;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderType;
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
public class UpdateProviderAction {

    private final ProviderRepository providerRepository;
    private final AiAgentActivityLogger activityLogger;

    public UpdateProviderAction(ProviderRepository providerRepository,
                                AiAgentActivityLogger activityLogger) {
        this.providerRepository = providerRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProviderDetailResponse execute(UpdateProviderCommand command) {
        Provider provider = findOrThrow(command.id());
        ProviderType type = AiAgentEnumParser.parseRequired(
                ProviderType.class, command.type(),
                AiAgentErrorCatalog.INVALID_PROVIDER_TYPE.code(), "type");
        provider.update(command.name(), type, command.apiBaseUrl(), command.description());
        Provider saved = providerRepository.save(provider);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER, saved.id(),
                AiAgentActivityActions.UPDATE_PROVIDER,
                "Provider updated: " + saved.code().value());

        return ProviderDetailResponse.from(saved);
    }

    private Provider findOrThrow(UUID id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.providerNotFound(id));
    }
}

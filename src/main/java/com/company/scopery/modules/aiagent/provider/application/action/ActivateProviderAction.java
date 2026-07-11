package com.company.scopery.modules.aiagent.provider.application.action;

import com.company.scopery.modules.aiagent.provider.application.command.ActivateProviderCommand;
import com.company.scopery.modules.aiagent.provider.application.response.ProviderDetailResponse;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
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
public class ActivateProviderAction {

    private final ProviderRepository providerRepository;
    private final AiAgentActivityLogger activityLogger;

    public ActivateProviderAction(ProviderRepository providerRepository,
                                  AiAgentActivityLogger activityLogger) {
        this.providerRepository = providerRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProviderDetailResponse execute(ActivateProviderCommand command) {
        Provider provider = findOrThrow(command.id());

        if (provider.status() == ProviderStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedProviderCannotBeActivated(provider.code().value());
        }
        if (provider.apiBaseUrl() == null || provider.apiBaseUrl().isBlank()) {
            throw AiAgentExceptions.providerRequiresApiBaseUrl(provider.code().value());
        }

        provider.activate();
        Provider saved = providerRepository.save(provider);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER, saved.id(),
                AiAgentActivityActions.ACTIVATE_PROVIDER,
                "Provider activated: " + saved.code().value());

        return ProviderDetailResponse.from(saved);
    }

    private Provider findOrThrow(UUID id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.providerNotFound(id));
    }
}

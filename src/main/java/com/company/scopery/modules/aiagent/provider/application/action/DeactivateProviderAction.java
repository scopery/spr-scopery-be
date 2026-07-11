package com.company.scopery.modules.aiagent.provider.application.action;

import com.company.scopery.modules.aiagent.provider.application.command.DeactivateProviderCommand;
import com.company.scopery.modules.aiagent.provider.application.response.ProviderDetailResponse;
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
public class DeactivateProviderAction {

    private final ProviderRepository providerRepository;
    private final AiAgentActivityLogger activityLogger;

    public DeactivateProviderAction(ProviderRepository providerRepository,
                                    AiAgentActivityLogger activityLogger) {
        this.providerRepository = providerRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProviderDetailResponse execute(DeactivateProviderCommand command) {
        Provider provider = findOrThrow(command.id());
        provider.deactivate();
        Provider saved = providerRepository.save(provider);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER, saved.id(),
                AiAgentActivityActions.DEACTIVATE_PROVIDER,
                "Provider deactivated: " + saved.code().value());

        return ProviderDetailResponse.from(saved);
    }

    private Provider findOrThrow(UUID id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.providerNotFound(id));
    }
}

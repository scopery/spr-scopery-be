package com.company.scopery.modules.aiagent.providersecret.application.action;

import com.company.scopery.modules.aiagent.providersecret.application.command.DeactivateProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretResponse;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecret;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecretRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeactivateProviderSecretAction {

    private final ProviderSecretRepository providerSecretRepository;
    private final AiAgentActivityLogger activityLogger;

    public DeactivateProviderSecretAction(ProviderSecretRepository providerSecretRepository,
                                          AiAgentActivityLogger activityLogger) {
        this.providerSecretRepository = providerSecretRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProviderSecretResponse execute(DeactivateProviderSecretCommand command) {
        ProviderSecret secret = providerSecretRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.providerSecretNotFound(command.id()));

        secret.deactivate();
        ProviderSecret saved = providerSecretRepository.save(secret);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER_SECRET, saved.id(),
                AiAgentActivityActions.DEACTIVATE_PROVIDER_SECRET,
                "Provider secret deactivated: " + command.id());

        return ProviderSecretResponse.from(saved);
    }
}

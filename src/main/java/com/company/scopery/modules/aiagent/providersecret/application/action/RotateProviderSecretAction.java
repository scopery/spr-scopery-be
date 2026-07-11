package com.company.scopery.modules.aiagent.providersecret.application.action;

import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.providersecret.application.command.RotateProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretResponse;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecret;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecretRepository;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.EncryptedSecret;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.SecretEncryptor;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.SecretMasker;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RotateProviderSecretAction {

    private final ProviderSecretRepository providerSecretRepository;
    private final ProviderRepository providerRepository;
    private final SecretEncryptor secretEncryptor;
    private final AiAgentActivityLogger activityLogger;

    public RotateProviderSecretAction(ProviderSecretRepository providerSecretRepository,
                                      ProviderRepository providerRepository,
                                      SecretEncryptor secretEncryptor,
                                      AiAgentActivityLogger activityLogger) {
        this.providerSecretRepository = providerSecretRepository;
        this.providerRepository = providerRepository;
        this.secretEncryptor = secretEncryptor;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProviderSecretResponse execute(RotateProviderSecretCommand command) {
        ProviderSecret existing = providerSecretRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.providerSecretNotFound(command.id()));

        Provider provider = loadActiveProvider(existing.providerId());

        EncryptedSecret encrypted = secretEncryptor.encrypt(command.secretValue());
        String maskedValue = SecretMasker.mask(command.secretValue());

        existing.deactivate();
        providerSecretRepository.save(existing);

        ProviderSecret rotated = ProviderSecret.create(
                existing.providerId(), existing.secretType(),
                encrypted.encryptedValue(), encrypted.iv(), encrypted.keyVersion(),
                maskedValue,
                command.description() != null ? command.description() : existing.description());
        ProviderSecret saved = providerSecretRepository.save(rotated);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER_SECRET, saved.id(),
                AiAgentActivityActions.ROTATE_PROVIDER_SECRET,
                "Provider secret rotated for provider: " + provider.code().value()
                        + " type: " + existing.secretType().name());

        return ProviderSecretResponse.from(saved);
    }

    private Provider loadActiveProvider(UUID providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> AiAgentExceptions.providerSecretProviderNotFound(providerId));
        if (provider.status() != ProviderStatus.ACTIVE) {
            throw AiAgentExceptions.providerSecretProviderNotActive(providerId);
        }
        return provider;
    }
}

package com.company.scopery.modules.aiagent.providersecret.application.action;

import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.providersecret.application.command.SetProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretResponse;
import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretType;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecret;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecretRepository;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.EncryptedSecret;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.SecretEncryptor;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.SecretMasker;
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
public class SetProviderSecretAction {

    private final ProviderSecretRepository providerSecretRepository;
    private final ProviderRepository providerRepository;
    private final SecretEncryptor secretEncryptor;
    private final AiAgentActivityLogger activityLogger;

    public SetProviderSecretAction(ProviderSecretRepository providerSecretRepository,
                                   ProviderRepository providerRepository,
                                   SecretEncryptor secretEncryptor,
                                   AiAgentActivityLogger activityLogger) {
        this.providerSecretRepository = providerSecretRepository;
        this.providerRepository = providerRepository;
        this.secretEncryptor = secretEncryptor;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProviderSecretResponse execute(SetProviderSecretCommand command) {
        Provider provider = loadActiveProvider(command.providerId());

        ProviderSecretType secretType = AiAgentEnumParser.parseRequired(
                ProviderSecretType.class, command.secretType(),
                AiAgentErrorCatalog.INVALID_PROVIDER_SECRET_TYPE.code(), "secretType");

        EncryptedSecret encrypted = secretEncryptor.encrypt(command.secretValue());
        String maskedValue = SecretMasker.mask(command.secretValue());

        providerSecretRepository.findActiveByProviderIdAndSecretType(command.providerId(), secretType)
                .ifPresent(existing -> {
                    existing.deactivate();
                    providerSecretRepository.save(existing);
                });

        ProviderSecret secret = ProviderSecret.create(
                command.providerId(), secretType,
                encrypted.encryptedValue(), encrypted.iv(), encrypted.keyVersion(),
                maskedValue, command.description());
        ProviderSecret saved = providerSecretRepository.save(secret);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER_SECRET, saved.id(),
                AiAgentActivityActions.SET_PROVIDER_SECRET,
                "Provider secret set for provider: " + provider.code().value() + " type: " + secretType.name());

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

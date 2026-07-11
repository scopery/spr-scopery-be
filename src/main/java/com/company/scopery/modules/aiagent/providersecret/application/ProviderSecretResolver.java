package com.company.scopery.modules.aiagent.providersecret.application;

import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretType;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecret;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecretRepository;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.EncryptedSecret;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.SecretEncryptor;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProviderSecretResolver {

    private final ProviderSecretRepository providerSecretRepository;
    private final SecretEncryptor secretEncryptor;

    public ProviderSecretResolver(ProviderSecretRepository providerSecretRepository,
                                   SecretEncryptor secretEncryptor) {
        this.providerSecretRepository = providerSecretRepository;
        this.secretEncryptor = secretEncryptor;
    }

    public String resolveApiKey(UUID providerId) {
        ProviderSecret secret = providerSecretRepository
                .findActiveByProviderIdAndSecretType(providerId, ProviderSecretType.API_KEY)
                .orElseThrow(() -> AiAgentExceptions.providerSecretNotFound(providerId));

        return secretEncryptor.decrypt(
                new EncryptedSecret(secret.encryptedValue(), secret.iv(), secret.keyVersion()));
    }
}

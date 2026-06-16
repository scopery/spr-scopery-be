package com.company.scopery.modules.aiagent.providersecret.application;

import com.company.scopery.common.pagination.PageResponse;
import com.company.scopery.modules.aiagent.provider.domain.Provider;
import com.company.scopery.modules.aiagent.provider.domain.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.ProviderStatus;
import com.company.scopery.modules.aiagent.providersecret.application.command.DeactivateProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.command.RotateProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.command.SetProviderSecretCommand;
import com.company.scopery.modules.aiagent.providersecret.application.query.GetProviderSecretDetailQuery;
import com.company.scopery.modules.aiagent.providersecret.application.query.SearchProviderSecretQuery;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretDetailResponse;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretResponse;
import com.company.scopery.modules.aiagent.providersecret.domain.ProviderSecret;
import com.company.scopery.modules.aiagent.providersecret.domain.ProviderSecretRepository;
import com.company.scopery.modules.aiagent.providersecret.domain.ProviderSecretStatus;
import com.company.scopery.modules.aiagent.providersecret.domain.ProviderSecretType;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.EncryptedSecret;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.SecretEncryptor;
import com.company.scopery.modules.aiagent.providersecret.infrastructure.crypto.SecretMasker;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProviderSecretApplicationService {

    private final ProviderSecretRepository providerSecretRepository;
    private final ProviderRepository providerRepository;
    private final SecretEncryptor secretEncryptor;
    private final AiAgentActivityLogger activityLogger;

    public ProviderSecretApplicationService(ProviderSecretRepository providerSecretRepository,
                                            ProviderRepository providerRepository,
                                            SecretEncryptor secretEncryptor,
                                            AiAgentActivityLogger activityLogger) {
        this.providerSecretRepository = providerSecretRepository;
        this.providerRepository = providerRepository;
        this.secretEncryptor = secretEncryptor;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProviderSecretResponse setProviderSecret(SetProviderSecretCommand command) {
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

    @Transactional
    public ProviderSecretResponse rotateProviderSecret(RotateProviderSecretCommand command) {
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

    @Transactional
    public ProviderSecretResponse deactivateProviderSecret(DeactivateProviderSecretCommand command) {
        ProviderSecret secret = providerSecretRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.providerSecretNotFound(command.id()));

        secret.deactivate();
        ProviderSecret saved = providerSecretRepository.save(secret);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER_SECRET, saved.id(),
                AiAgentActivityActions.DEACTIVATE_PROVIDER_SECRET,
                "Provider secret deactivated: " + command.id());

        return ProviderSecretResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ProviderSecretDetailResponse getProviderSecretDetail(GetProviderSecretDetailQuery query) {
        ProviderSecret secret = providerSecretRepository.findById(query.id())
                .orElseThrow(() -> AiAgentExceptions.providerSecretNotFound(query.id()));
        return ProviderSecretDetailResponse.from(secret);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProviderSecretResponse> searchProviderSecrets(SearchProviderSecretQuery query) {
        ProviderSecretType secretType = AiAgentEnumParser.parseOptional(
                ProviderSecretType.class, query.secretType(),
                AiAgentErrorCatalog.INVALID_PROVIDER_SECRET_TYPE.code(), "secretType");
        ProviderSecretStatus status = AiAgentEnumParser.parseOptional(
                ProviderSecretStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_PROVIDER_SECRET_STATUS.code(), "status");

        PageRequest pageRequest = PageRequest.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<ProviderSecretResponse> page = providerSecretRepository
                .findAll(query.providerId(), secretType, status, pageRequest)
                .map(ProviderSecretResponse::from);

        return PageResponse.from(page);
    }

    private Provider loadActiveProvider(java.util.UUID providerId) {
        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> AiAgentExceptions.providerSecretProviderNotFound(providerId));
        if (provider.status() != ProviderStatus.ACTIVE) {
            throw AiAgentExceptions.providerSecretProviderNotActive(providerId);
        }
        return provider;
    }
}

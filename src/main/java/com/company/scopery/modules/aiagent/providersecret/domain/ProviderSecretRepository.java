package com.company.scopery.modules.aiagent.providersecret.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProviderSecretRepository {

    ProviderSecret save(ProviderSecret secret);

    Optional<ProviderSecret> findById(UUID id);

    Optional<ProviderSecret> findActiveByProviderIdAndSecretType(UUID providerId, ProviderSecretType secretType);

    Page<ProviderSecret> findAll(UUID providerId, ProviderSecretType secretType,
                                  ProviderSecretStatus status, Pageable pageable);
}

package com.company.scopery.modules.aiagent.providersecret.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretStatus;
import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretType;

import java.util.Optional;
import java.util.UUID;

public interface ProviderSecretRepository {

    ProviderSecret save(ProviderSecret secret);

    Optional<ProviderSecret> findById(UUID id);

    Optional<ProviderSecret> findActiveByProviderIdAndSecretType(UUID providerId, ProviderSecretType secretType);

    Optional<ProviderSecret> findActiveByProviderCodeAndSecretType(String providerCode, ProviderSecretType secretType);

    PageResult<ProviderSecret> findAll(UUID providerId, ProviderSecretType secretType,
                                       ProviderSecretStatus status, PageQuery pageQuery);
}

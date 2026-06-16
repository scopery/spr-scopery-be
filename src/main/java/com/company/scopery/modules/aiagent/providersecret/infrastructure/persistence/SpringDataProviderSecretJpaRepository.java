package com.company.scopery.modules.aiagent.providersecret.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataProviderSecretJpaRepository
        extends JpaRepository<ProviderSecretJpaEntity, UUID>, JpaSpecificationExecutor<ProviderSecretJpaEntity> {

    Optional<ProviderSecretJpaEntity> findByProviderIdAndSecretTypeAndStatus(
            UUID providerId, String secretType, String status);
}

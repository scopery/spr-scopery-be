package com.company.scopery.modules.aiagent.providersecret.infrastructure.persistence;

import com.company.scopery.modules.aiagent.providersecret.infrastructure.persistence.entity.ProviderSecretJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataProviderSecretJpaRepository
        extends JpaRepository<ProviderSecretJpaEntity, UUID>, JpaSpecificationExecutor<ProviderSecretJpaEntity> {

    Optional<ProviderSecretJpaEntity> findByProviderIdAndSecretTypeAndStatus(
            UUID providerId, String secretType, String status);

    @Query(value = """
            SELECT ps.* FROM aiagent_provider_secret ps
            JOIN aiagent_provider p ON ps.provider_id = p.id
            WHERE p.code = :providerCode
            AND ps.secret_type = :secretType
            AND ps.status = :status
            LIMIT 1
            """, nativeQuery = true)
    Optional<ProviderSecretJpaEntity> findByProviderCodeAndSecretTypeAndStatus(
            @Param("providerCode") String providerCode,
            @Param("secretType") String secretType,
            @Param("status") String status);
}

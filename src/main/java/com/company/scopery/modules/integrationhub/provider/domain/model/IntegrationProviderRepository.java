package com.company.scopery.modules.integrationhub.provider.domain.model;
import java.util.List; import java.util.Optional;
public interface IntegrationProviderRepository {
    IntegrationProvider save(IntegrationProvider p);
    Optional<IntegrationProvider> findByCode(String code);
    List<IntegrationProvider> findAll();
    boolean existsByCode(String code);
}

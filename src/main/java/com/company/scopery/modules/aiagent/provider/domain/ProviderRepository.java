package com.company.scopery.modules.aiagent.provider.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProviderRepository {

    Provider save(Provider provider);

    Optional<Provider> findById(UUID id);

    boolean existsByCode(ProviderCode code);

    Page<Provider> findAll(String keyword, String type, ProviderStatus status, Pageable pageable);
}
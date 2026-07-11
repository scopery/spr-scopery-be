package com.company.scopery.modules.aiagent.provider.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderType;
import com.company.scopery.modules.aiagent.provider.domain.valueobject.ProviderCode;

import java.util.Optional;
import java.util.UUID;

public interface ProviderRepository {

    Provider save(Provider provider);

    Optional<Provider> findById(UUID id);

    boolean existsByCode(ProviderCode code);

    PageResult<Provider> findAll(String keyword, ProviderType type, ProviderStatus status, PageQuery pageQuery);
}

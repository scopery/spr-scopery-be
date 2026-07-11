package com.company.scopery.modules.aiagent.provider.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.provider.application.query.GetProviderDetailQuery;
import com.company.scopery.modules.aiagent.provider.application.query.SearchProviderQuery;
import com.company.scopery.modules.aiagent.provider.application.response.ProviderDetailResponse;
import com.company.scopery.modules.aiagent.provider.application.response.ProviderResponse;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderStatus;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderType;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProviderQueryService {

    private final ProviderRepository providerRepository;

    public ProviderQueryService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Transactional(readOnly = true)
    public ProviderDetailResponse getProviderDetail(GetProviderDetailQuery query) {
        Provider provider = findOrThrow(query.id());
        return ProviderDetailResponse.from(provider);
    }

    @Transactional(readOnly = true)
    public PageResult<ProviderResponse> searchProviders(SearchProviderQuery query) {
        ProviderStatus status = AiAgentEnumParser.parseOptional(
                ProviderStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_PROVIDER_STATUS.code(), "status");
        ProviderType type = AiAgentEnumParser.parseOptional(
                ProviderType.class, query.type(),
                AiAgentErrorCatalog.INVALID_PROVIDER_TYPE.code(), "type");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);
        return providerRepository
                .findAll(query.keyword(), type, status, pageQuery)
                .map(ProviderResponse::from);
    }

    private Provider findOrThrow(UUID id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.providerNotFound(id));
    }
}

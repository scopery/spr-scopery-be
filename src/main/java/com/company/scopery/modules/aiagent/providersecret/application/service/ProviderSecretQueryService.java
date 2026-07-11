package com.company.scopery.modules.aiagent.providersecret.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.providersecret.application.query.GetProviderSecretDetailQuery;
import com.company.scopery.modules.aiagent.providersecret.application.query.SearchProviderSecretQuery;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretDetailResponse;
import com.company.scopery.modules.aiagent.providersecret.application.response.ProviderSecretResponse;
import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretStatus;
import com.company.scopery.modules.aiagent.providersecret.domain.enums.ProviderSecretType;
import com.company.scopery.modules.aiagent.providersecret.domain.model.ProviderSecretRepository;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProviderSecretQueryService {

    private final ProviderSecretRepository providerSecretRepository;

    public ProviderSecretQueryService(ProviderSecretRepository providerSecretRepository) {
        this.providerSecretRepository = providerSecretRepository;
    }

    @Transactional(readOnly = true)
    public ProviderSecretDetailResponse getProviderSecretDetail(GetProviderSecretDetailQuery query) {
        return ProviderSecretDetailResponse.from(
                providerSecretRepository.findById(query.id())
                        .orElseThrow(() -> AiAgentExceptions.providerSecretNotFound(query.id())));
    }

    @Transactional(readOnly = true)
    public PageResult<ProviderSecretResponse> searchProviderSecrets(SearchProviderSecretQuery query) {
        ProviderSecretType secretType = AiAgentEnumParser.parseOptional(
                ProviderSecretType.class, query.secretType(),
                AiAgentErrorCatalog.INVALID_PROVIDER_SECRET_TYPE.code(), "secretType");
        ProviderSecretStatus status = AiAgentEnumParser.parseOptional(
                ProviderSecretStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_PROVIDER_SECRET_STATUS.code(), "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);

        return providerSecretRepository
                .findAll(query.providerId(), secretType, status, pageQuery)
                .map(ProviderSecretResponse::from);
    }
}

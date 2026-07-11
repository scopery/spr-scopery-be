package com.company.scopery.modules.aiagent.aimodel.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.aimodel.application.query.GetAiModelDetailQuery;
import com.company.scopery.modules.aiagent.aimodel.application.query.SearchAiModelQuery;
import com.company.scopery.modules.aiagent.aimodel.application.response.AiModelDetailResponse;
import com.company.scopery.modules.aiagent.aimodel.application.response.AiModelResponse;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelStatus;
import com.company.scopery.modules.aiagent.aimodel.domain.enums.AiModelType;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModel;
import com.company.scopery.modules.aiagent.aimodel.domain.model.AiModelRepository;
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
public class AiModelQueryService {

    private final AiModelRepository aiModelRepository;
    private final ProviderRepository providerRepository;

    public AiModelQueryService(AiModelRepository aiModelRepository,
                               ProviderRepository providerRepository) {
        this.aiModelRepository = aiModelRepository;
        this.providerRepository = providerRepository;
    }

    @Transactional(readOnly = true)
    public AiModelDetailResponse getAiModelDetail(GetAiModelDetailQuery query) {
        AiModel model = aiModelRepository.findById(query.id())
                .orElseThrow(() -> AiAgentExceptions.aiModelNotFound(query.id()));
        String providerName = loadProviderName(model.providerId());
        return AiModelDetailResponse.from(model, providerName);
    }

    @Transactional(readOnly = true)
    public PageResult<AiModelResponse> searchAiModels(SearchAiModelQuery query) {
        AiModelStatus status = AiAgentEnumParser.parseOptional(
                AiModelStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_AI_MODEL_STATUS.code(), "status");
        AiModelType type = AiAgentEnumParser.parseOptional(
                AiModelType.class, query.type(),
                AiAgentErrorCatalog.INVALID_AI_MODEL_TYPE.code(), "type");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);
        return aiModelRepository
                .findAll(query.providerId(), query.keyword(), status, type, pageQuery)
                .map(AiModelResponse::from);
    }

    private String loadProviderName(UUID providerId) {
        return providerRepository.findById(providerId)
                .map(Provider::name)
                .orElse(null);
    }
}

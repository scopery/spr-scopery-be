package com.company.scopery.modules.aiagent.aimodel.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.aiagent.aimodel.application.command.*;
import com.company.scopery.modules.aiagent.aimodel.application.query.*;
import com.company.scopery.modules.aiagent.aimodel.application.response.*;
import com.company.scopery.modules.aiagent.aimodel.domain.*;
import com.company.scopery.modules.aiagent.provider.domain.Provider;
import com.company.scopery.modules.aiagent.provider.domain.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.ProviderStatus;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AiModelApplicationService {

    private final AiModelRepository aiModelRepository;
    private final ProviderRepository providerRepository;
    private final AiAgentActivityLogger activityLogger;

    public AiModelApplicationService(AiModelRepository aiModelRepository,
                                      ProviderRepository providerRepository,
                                      AiAgentActivityLogger activityLogger) {
        this.aiModelRepository = aiModelRepository;
        this.providerRepository = providerRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiModelResponse createAiModel(CreateAiModelCommand command) {
        Provider provider = findProviderOrThrow(command.providerId());

        if (provider.status() != ProviderStatus.ACTIVE) {
            throw AiAgentExceptions.aiModelProviderNotActive(provider.code().value());
        }

        AiModelCode code = AiModelCode.of(command.code());

        if (aiModelRepository.existsByProviderIdAndCode(command.providerId(), code)) {
            throw AiAgentExceptions.aiModelCodeAlreadyExists(code.value());
        }

        AiModelType type = AiAgentEnumParser.parseRequired(
                AiModelType.class, command.type(),
                AiAgentErrorCatalog.INVALID_AI_MODEL_TYPE.code(), "type");

        AiModel model = AiModel.create(command.providerId(), command.name(), code,
                command.providerModelId(), type, command.description());

        AiModel saved = aiModelRepository.save(model);

        activityLogger.logSuccess(AiAgentEntityTypes.AI_MODEL, saved.id(),
                AiAgentActivityActions.CREATE_AI_MODEL,
                "AI model created: " + saved.code().value());

        return AiModelResponse.from(saved);
    }

    @Transactional
    public AiModelDetailResponse updateAiModel(UpdateAiModelCommand command) {
        AiModel model = findOrThrow(command.id());

        AiModelType type = AiAgentEnumParser.parseRequired(
                AiModelType.class, command.type(),
                AiAgentErrorCatalog.INVALID_AI_MODEL_TYPE.code(), "type");
        model.update(command.name(), command.providerModelId(), type, command.description());

        AiModel saved = aiModelRepository.save(model);

        activityLogger.logSuccess(AiAgentEntityTypes.AI_MODEL, saved.id(),
                AiAgentActivityActions.UPDATE_AI_MODEL,
                "AI model updated: " + saved.code().value());

        String providerName = loadProviderName(saved.providerId());
        return AiModelDetailResponse.from(saved, providerName);
    }

    @Transactional(readOnly = true)
    public AiModelDetailResponse getAiModelDetail(GetAiModelDetailQuery query) {
        AiModel model = findOrThrow(query.id());
        String providerName = loadProviderName(model.providerId());
        return AiModelDetailResponse.from(model, providerName);
    }

    @Transactional(readOnly = true)
    public Page<AiModelResponse> searchAiModels(SearchAiModelQuery query) {
        AiModelStatus status = AiAgentEnumParser.parseOptional(
                AiModelStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_AI_MODEL_STATUS.code(), "status");
        AiModelType type = AiAgentEnumParser.parseOptional(
                AiModelType.class, query.type(),
                AiAgentErrorCatalog.INVALID_AI_MODEL_TYPE.code(), "type");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, AiAgentSortFields.CREATED_AT));
        return aiModelRepository
                .findAll(query.providerId(), query.keyword(), status, type, pageable)
                .map(AiModelResponse::from);
    }

    @Transactional
    public AiModelDetailResponse activateAiModel(ActivateAiModelCommand command) {
        AiModel model = findOrThrow(command.id());

        if (model.status() == AiModelStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedAiModelCannotBeActivated(model.code().value());
        }

        Provider provider = findProviderOrThrow(model.providerId());

        if (provider.status() != ProviderStatus.ACTIVE) {
            throw AiAgentExceptions.aiModelProviderNotActive(provider.code().value());
        }

        model.activate();
        AiModel saved = aiModelRepository.save(model);

        activityLogger.logSuccess(AiAgentEntityTypes.AI_MODEL, saved.id(),
                AiAgentActivityActions.ACTIVATE_AI_MODEL,
                "AI model activated: " + saved.code().value());

        return AiModelDetailResponse.from(saved, provider.name());
    }

    @Transactional
    public AiModelDetailResponse deactivateAiModel(DeactivateAiModelCommand command) {
        AiModel model = findOrThrow(command.id());
        model.deactivate();
        AiModel saved = aiModelRepository.save(model);

        activityLogger.logSuccess(AiAgentEntityTypes.AI_MODEL, saved.id(),
                AiAgentActivityActions.DEACTIVATE_AI_MODEL,
                "AI model deactivated: " + saved.code().value());

        String providerName = loadProviderName(saved.providerId());
        return AiModelDetailResponse.from(saved, providerName);
    }

    private AiModel findOrThrow(UUID id) {
        return aiModelRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.aiModelNotFound(id));
    }

    private Provider findProviderOrThrow(UUID providerId) {
        return providerRepository.findById(providerId)
                .orElseThrow(() -> AiAgentExceptions.aiModelProviderNotFound(providerId));
    }

    private String loadProviderName(UUID providerId) {
        return providerRepository.findById(providerId)
                .map(Provider::name)
                .orElse(null);
    }
}
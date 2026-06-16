package com.company.scopery.modules.aiagent.provider.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.aiagent.provider.application.command.*;
import com.company.scopery.modules.aiagent.provider.application.query.*;
import com.company.scopery.modules.aiagent.provider.application.response.*;
import com.company.scopery.modules.aiagent.provider.domain.*;
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
public class ProviderApplicationService {

    private final ProviderRepository providerRepository;
    private final AiAgentActivityLogger activityLogger;

    public ProviderApplicationService(ProviderRepository providerRepository,
                                       AiAgentActivityLogger activityLogger) {
        this.providerRepository = providerRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProviderResponse createProvider(CreateProviderCommand command) {
        ProviderCode code = ProviderCode.of(command.code());

        if (providerRepository.existsByCode(code)) {
            throw AiAgentExceptions.providerCodeAlreadyExists(code.value());
        }

        Provider provider = Provider.create(
                command.name(), code, command.type(), command.apiBaseUrl(), command.description());

        Provider saved = providerRepository.save(provider);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER, saved.id(),
                AiAgentActivityActions.CREATE_PROVIDER,
                "Provider created: " + saved.code().value());

        return ProviderResponse.from(saved);
    }

    @Transactional
    public ProviderDetailResponse updateProvider(UpdateProviderCommand command) {
        Provider provider = findOrThrow(command.id());
        provider.update(command.name(), command.type(), command.apiBaseUrl(), command.description());
        Provider saved = providerRepository.save(provider);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER, saved.id(),
                AiAgentActivityActions.UPDATE_PROVIDER,
                "Provider updated: " + saved.code().value());

        return ProviderDetailResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ProviderDetailResponse getProviderDetail(GetProviderDetailQuery query) {
        Provider provider = findOrThrow(query.id());
        return ProviderDetailResponse.from(provider);
    }

    @Transactional(readOnly = true)
    public Page<ProviderResponse> searchProviders(SearchProviderQuery query) {
        ProviderStatus status = AiAgentEnumParser.parseOptional(
                ProviderStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_PROVIDER_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, AiAgentSortFields.CREATED_AT));
        return providerRepository
                .findAll(query.keyword(), query.type(), status, pageable)
                .map(ProviderResponse::from);
    }

    @Transactional
    public ProviderDetailResponse activateProvider(ActivateProviderCommand command) {
        Provider provider = findOrThrow(command.id());

        if (provider.status() == ProviderStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedProviderCannotBeActivated(provider.code().value());
        }
        if (provider.apiBaseUrl() == null || provider.apiBaseUrl().isBlank()) {
            throw AiAgentExceptions.providerRequiresApiBaseUrl(provider.code().value());
        }

        provider.activate();
        Provider saved = providerRepository.save(provider);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER, saved.id(),
                AiAgentActivityActions.ACTIVATE_PROVIDER,
                "Provider activated: " + saved.code().value());

        return ProviderDetailResponse.from(saved);
    }

    @Transactional
    public ProviderDetailResponse deactivateProvider(DeactivateProviderCommand command) {
        Provider provider = findOrThrow(command.id());
        provider.deactivate();
        Provider saved = providerRepository.save(provider);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER, saved.id(),
                AiAgentActivityActions.DEACTIVATE_PROVIDER,
                "Provider deactivated: " + saved.code().value());

        return ProviderDetailResponse.from(saved);
    }

    private Provider findOrThrow(UUID id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.providerNotFound(id));
    }
}

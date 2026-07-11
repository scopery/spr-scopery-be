package com.company.scopery.modules.aiagent.provider.application.action;

import com.company.scopery.modules.aiagent.provider.application.command.CreateProviderCommand;
import com.company.scopery.modules.aiagent.provider.application.response.ProviderResponse;
import com.company.scopery.modules.aiagent.provider.domain.enums.ProviderType;
import com.company.scopery.modules.aiagent.provider.domain.model.Provider;
import com.company.scopery.modules.aiagent.provider.domain.model.ProviderRepository;
import com.company.scopery.modules.aiagent.provider.domain.valueobject.ProviderCode;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateProviderAction {

    private final ProviderRepository providerRepository;
    private final AiAgentActivityLogger activityLogger;

    public CreateProviderAction(ProviderRepository providerRepository,
                                AiAgentActivityLogger activityLogger) {
        this.providerRepository = providerRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public ProviderResponse execute(CreateProviderCommand command) {
        ProviderCode code = ProviderCode.of(command.code());

        if (providerRepository.existsByCode(code)) {
            throw AiAgentExceptions.providerCodeAlreadyExists(code.value());
        }

        ProviderType type = AiAgentEnumParser.parseRequired(
                ProviderType.class, command.type(),
                AiAgentErrorCatalog.INVALID_PROVIDER_TYPE.code(), "type");

        Provider provider = Provider.create(
                command.name(), code, type, command.apiBaseUrl(), command.description());

        Provider saved = providerRepository.save(provider);

        activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER, saved.id(),
                AiAgentActivityActions.CREATE_PROVIDER,
                "Provider created: " + saved.code().value());

        return ProviderResponse.from(saved);
    }
}

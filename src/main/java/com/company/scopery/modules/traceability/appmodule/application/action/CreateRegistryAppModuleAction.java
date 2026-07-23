package com.company.scopery.modules.traceability.appmodule.application.action;

import com.company.scopery.modules.traceability.appmodule.application.command.CreateRegistryAppModuleCommand;
import com.company.scopery.modules.traceability.appmodule.application.response.RegistryAppModuleResponse;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModule;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModuleRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class CreateRegistryAppModuleAction {

    private final RegistryAppModuleRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final ApplicationEventPublisher publisher;

    public CreateRegistryAppModuleAction(RegistryAppModuleRepository repo,
                                         TraceabilityAuthorizationService authorization,
                                         ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.authorization = authorization;
        this.publisher = publisher;
    }

    @Transactional
    public RegistryAppModuleResponse execute(CreateRegistryAppModuleCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        RegistryAppModule saved = repo.save(
                RegistryAppModule.create(c.applicationId(), c.workspaceId(), c.code().trim(), c.name().trim(), c.description()));
        publisher.publishEvent(Map.of(
                "eventCode", "APP_MODULE_SAVED",
                "entityId", saved.id(),
                "workspaceId", saved.workspaceId()
        ));
        return RegistryAppModuleResponse.from(saved);
    }
}

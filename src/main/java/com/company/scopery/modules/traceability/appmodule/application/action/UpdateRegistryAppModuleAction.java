package com.company.scopery.modules.traceability.appmodule.application.action;

import com.company.scopery.modules.traceability.appmodule.application.command.UpdateRegistryAppModuleCommand;
import com.company.scopery.modules.traceability.appmodule.application.response.RegistryAppModuleResponse;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModuleRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class UpdateRegistryAppModuleAction {

    private final RegistryAppModuleRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final ApplicationEventPublisher publisher;

    public UpdateRegistryAppModuleAction(RegistryAppModuleRepository repo,
                                         TraceabilityAuthorizationService authorization,
                                         ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.authorization = authorization;
        this.publisher = publisher;
    }

    @Transactional
    public RegistryAppModuleResponse execute(UpdateRegistryAppModuleCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var module = repo.findByIdAndWorkspaceId(c.appModuleId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.appModuleNotFound(c.appModuleId()));
        var saved = repo.save(module.withUpdated(c.name(), c.description()));
        publisher.publishEvent(Map.of(
                "eventCode", "APP_MODULE_SAVED",
                "entityId", saved.id(),
                "workspaceId", saved.workspaceId()
        ));
        return RegistryAppModuleResponse.from(saved);
    }
}

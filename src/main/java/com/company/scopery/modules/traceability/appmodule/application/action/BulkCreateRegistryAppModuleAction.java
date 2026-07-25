package com.company.scopery.modules.traceability.appmodule.application.action;

import com.company.scopery.modules.traceability.appmodule.application.command.BulkCreateRegistryAppModuleCommand;
import com.company.scopery.modules.traceability.appmodule.application.command.CreateRegistryAppModuleCommand;
import com.company.scopery.modules.traceability.appmodule.application.response.RegistryAppModuleResponse;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModule;
import com.company.scopery.modules.traceability.appmodule.domain.model.RegistryAppModuleRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BulkCreateRegistryAppModuleAction {

    private final RegistryAppModuleRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final ApplicationEventPublisher publisher;

    public BulkCreateRegistryAppModuleAction(RegistryAppModuleRepository repo,
                                              TraceabilityAuthorizationService authorization,
                                              ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.authorization = authorization;
        this.publisher = publisher;
    }

    @Transactional
    public List<RegistryAppModuleResponse> execute(BulkCreateRegistryAppModuleCommand cmd) {
        authorization.requireWorkspaceCreate(cmd.workspaceId());

        List<RegistryAppModuleResponse> results = new ArrayList<>();

        for (CreateRegistryAppModuleCommand item : cmd.items()) {
            RegistryAppModule saved = repo.save(
                    RegistryAppModule.create(cmd.applicationId(), cmd.workspaceId(),
                            item.code().trim(), item.name().trim(), item.description()));
            publisher.publishEvent(Map.of(
                    "eventCode", "APP_MODULE_SAVED",
                    "entityId", saved.id(),
                    "workspaceId", saved.workspaceId()
            ));
            results.add(RegistryAppModuleResponse.from(saved));
        }

        return results;
    }
}

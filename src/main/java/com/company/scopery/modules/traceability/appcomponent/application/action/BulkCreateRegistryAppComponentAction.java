package com.company.scopery.modules.traceability.appcomponent.application.action;

import com.company.scopery.modules.traceability.appcomponent.application.command.BulkCreateRegistryAppComponentCommand;
import com.company.scopery.modules.traceability.appcomponent.application.response.RegistryAppComponentResponse;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class BulkCreateRegistryAppComponentAction {

    private final RegistryAppComponentRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public BulkCreateRegistryAppComponentAction(RegistryAppComponentRepository repo,
                                                TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional
    public List<RegistryAppComponentResponse> execute(BulkCreateRegistryAppComponentCommand cmd) {
        authorization.requireWorkspaceCreate(cmd.workspaceId());

        List<RegistryAppComponentResponse> results = new ArrayList<>();
        for (var item : cmd.items()) {
            results.add(RegistryAppComponentResponse.from(repo.save(
                    RegistryAppComponent.create(cmd.applicationId(), cmd.workspaceId(),
                            item.code().trim(), item.name().trim(), item.description(), item.componentType(),
                            "NONE", null, null, null, null)
            )));
        }
        return results;
    }
}

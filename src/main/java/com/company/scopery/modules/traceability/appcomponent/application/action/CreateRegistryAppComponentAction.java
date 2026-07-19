package com.company.scopery.modules.traceability.appcomponent.application.action;
import com.company.scopery.modules.traceability.appcomponent.application.command.CreateRegistryAppComponentCommand;
import com.company.scopery.modules.traceability.appcomponent.application.response.RegistryAppComponentResponse;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponent;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRegistryAppComponentAction {
    private final RegistryAppComponentRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public CreateRegistryAppComponentAction(RegistryAppComponentRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryAppComponentResponse execute(CreateRegistryAppComponentCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        return RegistryAppComponentResponse.from(repo.save(RegistryAppComponent.create(c.applicationId(), c.workspaceId(), c.code().trim(), c.name().trim(), c.description(), c.componentType())));
    }
}

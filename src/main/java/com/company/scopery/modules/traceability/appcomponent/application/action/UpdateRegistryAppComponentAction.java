package com.company.scopery.modules.traceability.appcomponent.application.action;
import com.company.scopery.modules.traceability.appcomponent.application.command.UpdateRegistryAppComponentCommand;
import com.company.scopery.modules.traceability.appcomponent.application.response.RegistryAppComponentResponse;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateRegistryAppComponentAction {
    private final RegistryAppComponentRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public UpdateRegistryAppComponentAction(RegistryAppComponentRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryAppComponentResponse execute(UpdateRegistryAppComponentCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var component = repo.findByIdAndWorkspaceId(c.appComponentId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.appComponentNotFound(c.appComponentId()));
        return RegistryAppComponentResponse.from(repo.save(component.withUpdated(c.name(), c.description(), c.componentType())));
    }
}

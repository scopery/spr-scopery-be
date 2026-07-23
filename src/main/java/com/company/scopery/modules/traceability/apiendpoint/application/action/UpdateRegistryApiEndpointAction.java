package com.company.scopery.modules.traceability.apiendpoint.application.action;
import com.company.scopery.modules.traceability.apiendpoint.application.command.UpdateRegistryApiEndpointCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.response.RegistryApiEndpointResponse;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpointRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class UpdateRegistryApiEndpointAction {
    private final RegistryApiEndpointRepository repo;
    private final TraceabilityAuthorizationService authorization;
    public UpdateRegistryApiEndpointAction(RegistryApiEndpointRepository repo, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.authorization=authorization;
    }
    @Transactional
    public RegistryApiEndpointResponse execute(UpdateRegistryApiEndpointCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        var endpoint = repo.findByIdAndApplicationId(c.endpointId(), c.applicationId())
                .orElseThrow(() -> TraceabilityExceptions.apiEndpointNotFound(c.endpointId()));
        return RegistryApiEndpointResponse.from(repo.save(endpoint.withUpdated(c.method(), c.pathPattern(), c.name())));
    }
}

package com.company.scopery.modules.traceability.apiendpoint.application.action;
import com.company.scopery.modules.traceability.application.domain.model.RegistryApplicationRepository;
import com.company.scopery.modules.traceability.apiendpoint.application.command.CreateRegistryApiEndpointCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.response.RegistryApiEndpointResponse;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpoint;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpointRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateRegistryApiEndpointAction {
    private final RegistryApiEndpointRepository repo;
    private final RegistryApplicationRepository applications;
    private final TraceabilityAuthorizationService authorization;
    public CreateRegistryApiEndpointAction(RegistryApiEndpointRepository repo, RegistryApplicationRepository applications, TraceabilityAuthorizationService authorization) {
        this.repo=repo; this.applications=applications; this.authorization=authorization;
    }
    @Transactional
    public RegistryApiEndpointResponse execute(CreateRegistryApiEndpointCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());
        applications.findByIdAndWorkspaceId(c.applicationId(), c.workspaceId()).orElseThrow(() -> TraceabilityExceptions.applicationNotFound(c.applicationId()));
        return RegistryApiEndpointResponse.from(repo.save(RegistryApiEndpoint.create(c.applicationId(), c.projectId(), c.method().trim().toUpperCase(), c.pathPattern().trim(), c.name())));
    }
}

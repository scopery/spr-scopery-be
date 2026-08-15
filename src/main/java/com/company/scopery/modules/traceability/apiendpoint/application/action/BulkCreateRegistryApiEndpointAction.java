package com.company.scopery.modules.traceability.apiendpoint.application.action;

import com.company.scopery.modules.traceability.application.domain.model.RegistryApplicationRepository;
import com.company.scopery.modules.traceability.apiendpoint.application.command.BulkCreateRegistryApiEndpointCommand;
import com.company.scopery.modules.traceability.apiendpoint.application.response.RegistryApiEndpointResponse;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpoint;
import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpointRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class BulkCreateRegistryApiEndpointAction {

    private final RegistryApiEndpointRepository repo;
    private final RegistryApplicationRepository applications;
    private final TraceabilityAuthorizationService authorization;

    public BulkCreateRegistryApiEndpointAction(RegistryApiEndpointRepository repo,
                                               RegistryApplicationRepository applications,
                                               TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.applications = applications;
        this.authorization = authorization;
    }

    @Transactional
    public List<RegistryApiEndpointResponse> execute(BulkCreateRegistryApiEndpointCommand cmd) {
        authorization.requireWorkspaceCreate(cmd.workspaceId());
        applications.findByIdAndWorkspaceId(cmd.applicationId(), cmd.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.applicationNotFound(cmd.applicationId()));

        List<RegistryApiEndpointResponse> results = new ArrayList<>();
        for (var item : cmd.items()) {
            results.add(RegistryApiEndpointResponse.from(repo.save(
                    RegistryApiEndpoint.create(cmd.applicationId(), item.projectId(),
                            item.method().trim().toUpperCase(), item.pathPattern().trim(), item.name(), null, null, null)
            )));
        }
        return results;
    }
}

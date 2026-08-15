package com.company.scopery.modules.traceability.componentapi.application.action;

import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpointRepository;
import com.company.scopery.modules.traceability.appcomponent.domain.model.RegistryAppComponentRepository;
import com.company.scopery.modules.traceability.application.domain.model.RegistryApplicationRepository;
import com.company.scopery.modules.traceability.componentapi.application.command.CreateRegistryComponentApiCommand;
import com.company.scopery.modules.traceability.componentapi.application.response.RegistryComponentApiResponse;
import com.company.scopery.modules.traceability.componentapi.domain.model.RegistryComponentApi;
import com.company.scopery.modules.traceability.componentapi.domain.model.RegistryComponentApiRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateRegistryComponentApiAction {

    private final RegistryComponentApiRepository repo;
    private final RegistryAppComponentRepository componentRepo;
    private final RegistryApiEndpointRepository apiEndpointRepo;
    private final RegistryApplicationRepository applicationRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public CreateRegistryComponentApiAction(RegistryComponentApiRepository repo,
                                             RegistryAppComponentRepository componentRepo,
                                             RegistryApiEndpointRepository apiEndpointRepo,
                                             RegistryApplicationRepository applicationRepo,
                                             TraceabilityAuthorizationService authorization,
                                             TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.componentRepo = componentRepo;
        this.apiEndpointRepo = apiEndpointRepo;
        this.applicationRepo = applicationRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public RegistryComponentApiResponse execute(CreateRegistryComponentApiCommand c) {
        authorization.requireWorkspaceCreate(c.workspaceId());

        componentRepo.findByIdAndWorkspaceId(c.componentId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.appComponentNotFound(c.componentId()));

        var apiEndpoint = apiEndpointRepo.findById(c.apiId())
                .orElseThrow(() -> TraceabilityExceptions.apiEndpointNotInWorkspace(c.apiId()));
        applicationRepo.findByIdAndWorkspaceId(apiEndpoint.applicationId(), c.workspaceId())
                .orElseThrow(() -> TraceabilityExceptions.apiEndpointNotInWorkspace(c.apiId()));

        if (repo.existsByComponentIdAndApiIdAndRole(c.componentId(), c.apiId(), c.role())) {
            throw TraceabilityExceptions.componentApiDuplicate(c.componentId(), c.apiId(), c.role().name());
        }

        var saved = repo.save(RegistryComponentApi.create(
                c.componentId(), c.apiId(), c.workspaceId(),
                c.role(), c.note(), c.displayOrder()));

        activityLogger.logSuccess(TraceabilityEntityTypes.COMPONENT_API, saved.id(),
                TraceabilityActivityActions.COMPONENT_API_LINKED,
                "API " + c.apiId() + " linked to component " + c.componentId() + " with role " + c.role());

        return RegistryComponentApiResponse.from(saved);
    }
}

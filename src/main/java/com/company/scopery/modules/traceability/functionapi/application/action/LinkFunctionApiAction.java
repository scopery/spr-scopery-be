package com.company.scopery.modules.traceability.functionapi.application.action;

import com.company.scopery.modules.traceability.apiendpoint.domain.model.RegistryApiEndpointRepository;
import com.company.scopery.modules.traceability.functionapi.application.command.LinkFunctionApiCommand;
import com.company.scopery.modules.traceability.functionapi.application.response.FunctionApiResponse;
import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApi;
import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApiRepository;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LinkFunctionApiAction {

    private final FunctionApiRepository repo;
    private final FunctionalItemRepository functionalItems;
    private final RegistryApiEndpointRepository apiEndpoints;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public LinkFunctionApiAction(FunctionApiRepository repo,
                                 FunctionalItemRepository functionalItems,
                                 RegistryApiEndpointRepository apiEndpoints,
                                 TraceabilityAuthorizationService authorization,
                                 TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.functionalItems = functionalItems;
        this.apiEndpoints = apiEndpoints;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FunctionApiResponse execute(LinkFunctionApiCommand c) {
        authorization.requireCreate(c.projectId());

        functionalItems.findByIdAndProjectId(c.functionalItemId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(c.functionalItemId()));

        apiEndpoints.findById(c.apiEndpointId())
                .orElseThrow(() -> TraceabilityExceptions.apiEndpointNotFound(c.apiEndpointId()));

        if (repo.existsByFunctionIdAndApiEndpointId(c.functionalItemId(), c.apiEndpointId())) {
            throw TraceabilityExceptions.functionApiDuplicate();
        }

        FunctionApi saved = repo.save(FunctionApi.create(c.functionalItemId(), c.apiEndpointId(), c.note()));

        activityLogger.logSuccess(TraceabilityEntityTypes.FUNCTION_API, c.functionalItemId(),
                TraceabilityActivityActions.FUNCTION_API_LINKED, "API endpoint linked to function");

        return FunctionApiResponse.from(saved);
    }
}

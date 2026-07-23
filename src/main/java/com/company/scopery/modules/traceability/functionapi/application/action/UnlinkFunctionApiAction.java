package com.company.scopery.modules.traceability.functionapi.application.action;

import com.company.scopery.modules.traceability.functionapi.domain.model.FunctionApiRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UnlinkFunctionApiAction {

    private final FunctionApiRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UnlinkFunctionApiAction(FunctionApiRepository repo,
                                   TraceabilityAuthorizationService authorization,
                                   TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID projectId, UUID functionId, UUID apiEndpointId) {
        authorization.requireCreate(projectId);

        if (!repo.existsByFunctionIdAndApiEndpointId(functionId, apiEndpointId)) {
            throw TraceabilityExceptions.functionApiNotFound(functionId, apiEndpointId);
        }

        repo.deleteByFunctionIdAndApiEndpointId(functionId, apiEndpointId);

        activityLogger.logSuccess(TraceabilityEntityTypes.FUNCTION_API, functionId,
                TraceabilityActivityActions.FUNCTION_API_UNLINKED, "API endpoint unlinked from function");
    }
}

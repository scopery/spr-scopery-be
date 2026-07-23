package com.company.scopery.modules.traceability.functionscreen.application.action;

import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreenRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UnlinkFunctionScreenAction {

    private final FunctionScreenRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UnlinkFunctionScreenAction(FunctionScreenRepository repo,
                                      TraceabilityAuthorizationService authorization,
                                      TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID projectId, UUID functionId, UUID screenId) {
        authorization.requireCreate(projectId);

        if (!repo.existsByFunctionIdAndScreenId(functionId, screenId)) {
            throw TraceabilityExceptions.functionScreenNotFound(functionId, screenId);
        }

        repo.deleteByFunctionIdAndScreenId(functionId, screenId);

        activityLogger.logSuccess(TraceabilityEntityTypes.FUNCTION_SCREEN, functionId,
                TraceabilityActivityActions.FUNCTION_SCREEN_UNLINKED, "Screen unlinked from function");
    }
}

package com.company.scopery.modules.traceability.functioncomm.application.action;

import com.company.scopery.modules.traceability.functioncomm.domain.model.FunctionCommunicationRepository;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UnlinkFunctionCommunicationAction {

    private final FunctionCommunicationRepository repo;
    private final FunctionalItemRepository functionalItems;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UnlinkFunctionCommunicationAction(FunctionCommunicationRepository repo,
                                             FunctionalItemRepository functionalItems,
                                             TraceabilityAuthorizationService authorization,
                                             TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.functionalItems = functionalItems;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID projectId, UUID functionalItemId, UUID communicationId) {
        authorization.requireCreate(projectId);

        functionalItems.findByIdAndProjectId(functionalItemId, projectId)
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(functionalItemId));

        if (!repo.existsByFunctionIdAndCommunicationId(functionalItemId, communicationId)) {
            throw TraceabilityExceptions.functionCommNotFound(functionalItemId, communicationId);
        }

        repo.deleteByFunctionIdAndCommunicationId(functionalItemId, communicationId);

        activityLogger.logSuccess(TraceabilityEntityTypes.FUNCTION_COMMUNICATION, functionalItemId,
                TraceabilityActivityActions.FUNCTION_COMM_UNLINKED, "Communication unlinked from function");
    }
}

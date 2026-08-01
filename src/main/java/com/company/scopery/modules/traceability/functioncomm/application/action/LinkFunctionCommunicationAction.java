package com.company.scopery.modules.traceability.functioncomm.application.action;

import com.company.scopery.modules.traceability.commspec.domain.model.CommunicationSpecificationRepository;
import com.company.scopery.modules.traceability.functioncomm.application.command.LinkFunctionCommunicationCommand;
import com.company.scopery.modules.traceability.functioncomm.application.response.FunctionCommunicationResponse;
import com.company.scopery.modules.traceability.functioncomm.domain.model.FunctionCommunication;
import com.company.scopery.modules.traceability.functioncomm.domain.model.FunctionCommunicationRepository;
import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LinkFunctionCommunicationAction {

    private final FunctionCommunicationRepository repo;
    private final FunctionalItemRepository functionalItems;
    private final CommunicationSpecificationRepository communicationSpecs;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public LinkFunctionCommunicationAction(FunctionCommunicationRepository repo,
                                           FunctionalItemRepository functionalItems,
                                           CommunicationSpecificationRepository communicationSpecs,
                                           TraceabilityAuthorizationService authorization,
                                           TraceabilityActivityLogger activityLogger) {
        this.repo = repo;
        this.functionalItems = functionalItems;
        this.communicationSpecs = communicationSpecs;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public FunctionCommunicationResponse execute(LinkFunctionCommunicationCommand c) {
        authorization.requireCreate(c.projectId());

        functionalItems.findByIdAndProjectId(c.functionalItemId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(c.functionalItemId()));

        communicationSpecs.findById(c.communicationId())
                .orElseThrow(() -> TraceabilityExceptions.commSpecNotFound(c.communicationId()));

        if (repo.existsByFunctionIdAndCommunicationId(c.functionalItemId(), c.communicationId())) {
            throw TraceabilityExceptions.functionCommDuplicate();
        }

        FunctionCommunication saved = repo.save(
                FunctionCommunication.create(c.functionalItemId(), c.communicationId(), c.note()));

        activityLogger.logSuccess(TraceabilityEntityTypes.FUNCTION_COMMUNICATION, c.functionalItemId(),
                TraceabilityActivityActions.FUNCTION_COMM_LINKED, "Communication linked to function");

        return FunctionCommunicationResponse.from(saved);
    }
}

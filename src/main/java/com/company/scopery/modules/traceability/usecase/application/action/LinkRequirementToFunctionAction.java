package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.usecase.application.command.LinkRequirementToFunctionCommand;
import com.company.scopery.modules.traceability.usecase.domain.model.RequirementFunctionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LinkRequirementToFunctionAction {

    private final FunctionalItemRepository functionalItems;
    private final RequirementFunctionRepository requirementFunctionRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public LinkRequirementToFunctionAction(FunctionalItemRepository functionalItems,
                                           RequirementFunctionRepository requirementFunctionRepo,
                                           TraceabilityAuthorizationService authorization,
                                           TraceabilityActivityLogger activityLogger) {
        this.functionalItems = functionalItems;
        this.requirementFunctionRepo = requirementFunctionRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(LinkRequirementToFunctionCommand c) {
        authorization.requireCreate(c.projectId());

        functionalItems.findByIdAndProjectId(c.functionId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(c.functionId()));

        // Idempotent: skip junction creation if already linked (FE may call twice)
        if (!requirementFunctionRepo.exists(c.requirementId(), c.functionId())) {
            requirementFunctionRepo.link(c.requirementId(), c.functionId());
            activityLogger.logSuccess(TraceabilityEntityTypes.FUNCTIONAL_ITEM, c.functionId(),
                    TraceabilityActivityActions.REQUIREMENT_FUNCTION_LINKED,
                    "Requirement linked to function: " + c.requirementId());
        }
        // COVERS trace link is created by the FE's second step (createTraceLink call)
        // via CreateTraceLinkAction, which handles JPA persistence correctly.
    }
}

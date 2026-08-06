package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.tracelink.domain.enums.TraceLinkType;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLink;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLinkRepository;
import com.company.scopery.modules.traceability.usecase.application.command.LinkRequirementToFunctionCommand;
import com.company.scopery.modules.traceability.usecase.domain.model.RequirementFunctionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LinkRequirementToFunctionAction {

    private final FunctionalItemRepository functionalItems;
    private final RequirementFunctionRepository requirementFunctionRepo;
    private final TraceLinkRepository traceLinkRepository;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public LinkRequirementToFunctionAction(FunctionalItemRepository functionalItems,
                                           RequirementFunctionRepository requirementFunctionRepo,
                                           TraceLinkRepository traceLinkRepository,
                                           TraceabilityAuthorizationService authorization,
                                           TraceabilityActivityLogger activityLogger) {
        this.functionalItems = functionalItems;
        this.requirementFunctionRepo = requirementFunctionRepo;
        this.traceLinkRepository = traceLinkRepository;
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

        // Always ensure a COVERS trace link exists so the panel can render the edge.
        if (!traceLinkRepository.existsActiveLink(c.projectId(), "REQUIREMENT", c.requirementId(),
                "FUNCTIONAL_ITEM", c.functionId(), TraceLinkType.COVERS.name())) {
            traceLinkRepository.save(TraceLink.create(c.projectId(), "REQUIREMENT", c.requirementId(),
                    "FUNCTIONAL_ITEM", c.functionId(), TraceLinkType.COVERS, null, null, null, null));
        }
    }
}

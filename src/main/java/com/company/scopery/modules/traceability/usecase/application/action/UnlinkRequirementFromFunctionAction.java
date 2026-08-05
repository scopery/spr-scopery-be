package com.company.scopery.modules.traceability.usecase.application.action;

import com.company.scopery.modules.traceability.functionalitem.domain.model.FunctionalItemRepository;
import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLinkRepository;
import com.company.scopery.modules.traceability.usecase.application.command.UnlinkRequirementFromFunctionCommand;
import com.company.scopery.modules.traceability.usecase.domain.model.RequirementFunctionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class UnlinkRequirementFromFunctionAction {

    private final FunctionalItemRepository functionalItems;
    private final RequirementFunctionRepository requirementFunctionRepo;
    private final RequirementRepository requirements;
    private final TraceLinkRepository traceLinkRepo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;

    public UnlinkRequirementFromFunctionAction(FunctionalItemRepository functionalItems,
                                               RequirementFunctionRepository requirementFunctionRepo,
                                               RequirementRepository requirements,
                                               TraceLinkRepository traceLinkRepo,
                                               TraceabilityAuthorizationService authorization,
                                               TraceabilityActivityLogger activityLogger) {
        this.functionalItems = functionalItems;
        this.requirementFunctionRepo = requirementFunctionRepo;
        this.requirements = requirements;
        this.traceLinkRepo = traceLinkRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UnlinkRequirementFromFunctionCommand c) {
        authorization.requireCreate(c.projectId());

        functionalItems.findByIdAndProjectId(c.functionId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.functionalItemNotFound(c.functionId()));

        if (requirementFunctionRepo.exists(c.requirementId(), c.functionId())) {
            requirementFunctionRepo.unlink(c.requirementId(), c.functionId());
        }

        // Archive any active COVERS trace link for this requirement→function pair so the
        // list API no longer returns it and the FE reflects the unlink immediately.
        traceLinkRepo.findActiveBySourceAndTarget(
                c.projectId(), "REQUIREMENT", c.requirementId(), "FUNCTIONAL_ITEM", c.functionId(), "COVERS"
        ).forEach(link -> traceLinkRepo.save(link.archive()));

        // Keep primary FK in sync: clear or repoint when it still referenced this function.
        // Idempotent when junction was already gone (FK-only / stale UI edge).
        requirements.findByIdAndProjectId(c.requirementId(), c.projectId()).ifPresent(req -> {
            if (!c.functionId().equals(req.functionalItemId())) {
                return;
            }
            List<UUID> remaining = requirementFunctionRepo.findFunctionIdsByRequirementId(c.requirementId());
            UUID nextPrimary = remaining.isEmpty() ? null : remaining.get(0);
            requirements.save(req.withFunctionalItemId(nextPrimary));
        });

        activityLogger.logSuccess(TraceabilityEntityTypes.FUNCTIONAL_ITEM, c.functionId(),
                TraceabilityActivityActions.REQUIREMENT_FUNCTION_UNLINKED,
                "Requirement unlinked from function: " + c.requirementId());
    }
}

package com.company.scopery.modules.traceability.requirement.application.action;

import com.company.scopery.modules.traceability.requirement.application.command.DeleteRequirementCommand;
import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.tracelink.domain.model.TraceLinkRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.RequirementFunctionRepository;
import com.company.scopery.modules.traceability.usecase.domain.model.RequirementUseCaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteRequirementAction {

    private final RequirementRepository requirements;
    private final RequirementFunctionRepository requirementFunctions;
    private final RequirementUseCaseRepository requirementUseCases;
    private final TraceLinkRepository traceLinks;
    private final TraceabilityAuthorizationService authorization;

    public DeleteRequirementAction(RequirementRepository requirements,
                                   RequirementFunctionRepository requirementFunctions,
                                   RequirementUseCaseRepository requirementUseCases,
                                   TraceLinkRepository traceLinks,
                                   TraceabilityAuthorizationService authorization) {
        this.requirements = requirements;
        this.requirementFunctions = requirementFunctions;
        this.requirementUseCases = requirementUseCases;
        this.traceLinks = traceLinks;
        this.authorization = authorization;
    }

    @Transactional
    public void execute(DeleteRequirementCommand c) {
        authorization.requireDelete(c.projectId());

        requirements.findByIdAndProjectId(c.requirementId(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.requirementNotFound(c.requirementId()));

        if (requirementFunctions.existsByRequirementId(c.requirementId())
                || requirementUseCases.existsByRequirementId(c.requirementId())
                || traceLinks.hasAnyActiveLinkForEntity("REQUIREMENT", c.requirementId())) {
            throw TraceabilityExceptions.requirementHasLinks(c.requirementId());
        }

        requirements.delete(c.requirementId());
    }
}

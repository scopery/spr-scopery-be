package com.company.scopery.modules.traceability.requirement.application.action;

import com.company.scopery.modules.traceability.requirement.application.command.UpdateRequirementCommand;
import com.company.scopery.modules.traceability.requirement.application.response.RequirementResponse;
import com.company.scopery.modules.traceability.requirement.domain.enums.RequirementPriority;
import com.company.scopery.modules.traceability.requirement.domain.enums.RequirementType;
import com.company.scopery.modules.traceability.requirement.domain.model.RequirementRepository;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityActivityActions;
import com.company.scopery.modules.traceability.shared.constant.TraceabilityEntityTypes;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class UpdateRequirementAction {

    private final RequirementRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;
    private final ApplicationEventPublisher publisher;

    public UpdateRequirementAction(RequirementRepository repo,
                                   TraceabilityAuthorizationService authorization,
                                   TraceabilityActivityLogger activityLogger,
                                   ApplicationEventPublisher publisher) {
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.publisher = publisher;
    }

    @Transactional
    public RequirementResponse execute(UpdateRequirementCommand c) {
        authorization.requireUpdate(c.projectId());
        var req = repo.findByIdAndProjectId(c.id(), c.projectId())
                .orElseThrow(() -> TraceabilityExceptions.requirementNotFound(c.id()));
        RequirementPriority priority = c.priority() != null
                ? TraceabilityEnumParser.parseRequired(RequirementPriority.class, c.priority(), "priority") : null;
        RequirementType type = c.requirementType() != null
                ? TraceabilityEnumParser.parseRequired(RequirementType.class, c.requirementType(), "requirementType") : null;
        var saved = repo.save(req.update(c.title(), c.description(), priority, type,
                c.applicationId(), c.functionalItemId(), c.nonFunctionalItemId()));
        activityLogger.logSuccess(TraceabilityEntityTypes.REQUIREMENT, saved.id(),
                TraceabilityActivityActions.REQUIREMENT_UPDATED, "Requirement updated");
        publisher.publishEvent(Map.of(
                "eventCode", "REQUIREMENT_SAVED",
                "entityId", saved.id(),
                "projectId", saved.projectId(),
                "workspaceId", saved.workspaceId()
        ));
        return RequirementResponse.from(saved);
    }
}

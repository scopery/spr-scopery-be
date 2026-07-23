package com.company.scopery.modules.traceability.requirement.application.action;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.traceability.requirement.application.command.CreateRequirementCommand;
import com.company.scopery.modules.traceability.requirement.application.response.RequirementResponse;
import com.company.scopery.modules.traceability.requirement.domain.enums.*;
import com.company.scopery.modules.traceability.requirement.domain.model.*;
import com.company.scopery.modules.traceability.shared.activity.TraceabilityActivityLogger;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.constant.*;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import com.company.scopery.modules.traceability.shared.util.TraceabilityEnumParser;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class CreateRequirementAction {

    private final ProjectRepository projects;
    private final RequirementRepository repo;
    private final TraceabilityAuthorizationService authorization;
    private final TraceabilityActivityLogger activityLogger;
    private final ApplicationEventPublisher publisher;

    public CreateRequirementAction(ProjectRepository projects, RequirementRepository repo,
                                   TraceabilityAuthorizationService authorization,
                                   TraceabilityActivityLogger activityLogger,
                                   ApplicationEventPublisher publisher) {
        this.projects = projects;
        this.repo = repo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.publisher = publisher;
    }

    @Transactional
    public RequirementResponse execute(CreateRequirementCommand c) {
        authorization.requireCreate(c.projectId());
        Project project = projects.findById(c.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw TraceabilityExceptions.projectArchived(c.projectId());
        if (c.title() == null || c.title().isBlank()) throw TraceabilityExceptions.titleRequired();
        var saved = repo.save(Requirement.create(
                project.id(), project.workspaceId(), c.applicationId(), c.code(), c.title().trim(), c.description(),
                TraceabilityEnumParser.parseRequired(RequirementType.class, c.requirementType(), "requirementType"),
                TraceabilityEnumParser.parseRequired(RequirementPriority.class, c.priority(), "priority"),
                c.functionalItemId(), c.nonFunctionalItemId()));
        activityLogger.logSuccess(TraceabilityEntityTypes.REQUIREMENT, saved.id(),
                TraceabilityActivityActions.REQUIREMENT_CREATED, "Requirement created");
        publisher.publishEvent(Map.of(
                "eventCode", "REQUIREMENT_SAVED",
                "entityId", saved.id(),
                "projectId", saved.projectId(),
                "workspaceId", saved.workspaceId()
        ));
        return RequirementResponse.from(saved);
    }
}

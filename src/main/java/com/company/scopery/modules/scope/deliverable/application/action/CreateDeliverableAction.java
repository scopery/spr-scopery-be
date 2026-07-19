package com.company.scopery.modules.scope.deliverable.application.action;

import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.scope.deliverable.application.command.CreateDeliverableCommand;
import com.company.scopery.modules.scope.deliverable.application.response.DeliverableResponse;
import com.company.scopery.modules.scope.deliverable.domain.enums.DeliverableType;
import com.company.scopery.modules.scope.deliverable.domain.model.Deliverable;
import com.company.scopery.modules.scope.deliverable.domain.model.DeliverableRepository;
import com.company.scopery.modules.scope.shared.activity.ScopeActivityLogger;
import com.company.scopery.modules.scope.shared.authorization.ScopeAuthorizationService;
import com.company.scopery.modules.scope.shared.constant.ScopeActivityActions;
import com.company.scopery.modules.scope.shared.constant.ScopeEntityTypes;
import com.company.scopery.modules.scope.shared.error.ScopeExceptions;
import com.company.scopery.modules.scope.shared.util.ScopeEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateDeliverableAction {
    private final ProjectRepository projects;
    private final DeliverableRepository deliverables;
    private final ScopeAuthorizationService authorization;
    private final ScopeActivityLogger activityLogger;

    public CreateDeliverableAction(ProjectRepository projects, DeliverableRepository deliverables,
                                   ScopeAuthorizationService authorization, ScopeActivityLogger activityLogger) {
        this.projects = projects;
        this.deliverables = deliverables;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public DeliverableResponse execute(CreateDeliverableCommand command) {
        authorization.requireDeliverableCreate(command.projectId());
        Project project = projects.findById(command.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(command.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ScopeExceptions.projectArchived(command.projectId());
        }
        DeliverableType deliverableType = ScopeEnumParser.parseRequired(DeliverableType.class, command.type(), "type");
        Deliverable d = deliverables.save(Deliverable.create(
                project.id(), project.workspaceId(), deliverableType, command.code(),
                command.title().trim(), command.description(),
                command.acceptanceRequired() == null || command.acceptanceRequired()));
        activityLogger.logSuccess(ScopeEntityTypes.DELIVERABLE, d.id(), ScopeActivityActions.DELIVERABLE_CREATED, "Deliverable created");
        return DeliverableResponse.from(d);
    }
}

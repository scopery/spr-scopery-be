package com.company.scopery.modules.projectbaseline.changerequest.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectbaseline.changerequest.application.command.CreateChangeRequestCommand;
import com.company.scopery.modules.projectbaseline.changerequest.application.response.ChangeRequestResponse;
import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangePriority;
import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangeType;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequest;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequestRepository;
import com.company.scopery.modules.projectbaseline.shared.activity.ProjectBaselineActivityLogger;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.*;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import com.company.scopery.modules.projectbaseline.shared.util.ProjectBaselineEnumParser;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateChangeRequestAction {
    private final ProjectRepository projects;
    private final ChangeRequestRepository changeRequests;
    private final ProjectBaselineAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;

    public CreateChangeRequestAction(ProjectRepository projects, ChangeRequestRepository changeRequests,
                                     ProjectBaselineAuthorizationService authorization,
                                     CurrentUserAuthorizationService currentUser,
                                     ProjectBaselinePlatformPublisher publisher,
                                     ProjectBaselineActivityLogger activityLogger) {
        this.projects = projects; this.changeRequests = changeRequests; this.authorization = authorization;
        this.currentUser = currentUser; this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ChangeRequestResponse execute(CreateChangeRequestCommand cmd) {
        authorization.requireChangeRequestCreate(cmd.projectId());
        Project project = projects.findById(cmd.projectId())
                .orElseThrow(() -> ProjectExceptions.projectNotFound(cmd.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) {
            throw ProjectBaselineExceptions.changeRequestProjectArchived(project.id());
        }
        if (changeRequests.existsByProjectIdAndCode(project.id(), cmd.code())) {
            throw ProjectBaselineExceptions.changeRequestCodeExists(cmd.code());
        }
        ChangeType type = ProjectBaselineEnumParser.parseRequired(ChangeType.class, cmd.changeType(),
                "CHANGE_TYPE_INVALID", "changeType");
        ChangePriority priority = ProjectBaselineEnumParser.parseOptional(ChangePriority.class, cmd.priority(),
                "CHANGE_PRIORITY_INVALID", "priority");
        java.util.UUID actorId = currentUser.resolveCurrentUser().id();
        ChangeRequest cr = ChangeRequest.create(project.id(), project.workspaceId(),
                cmd.baselineId() != null ? cmd.baselineId() : project.currentBaselineId(),
                cmd.code(), cmd.title(), cmd.description(), type, priority, cmd.reason(), actorId, MDC.get("traceId"));
        cr = changeRequests.save(cr);
        publisher.enqueueChangeRequest(cr, ProjectBaselineEventCodes.CHANGE_REQUEST_CREATED);
        activityLogger.logSuccess(ProjectBaselineEntityTypes.CHANGE_REQUEST, cr.id(),
                ProjectBaselineActivityActions.CHANGE_REQUEST_CREATED, "CHANGE_REQUEST_CREATED");
        return ChangeRequestResponse.from(cr);
    }
}

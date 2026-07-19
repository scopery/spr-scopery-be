package com.company.scopery.modules.clientportal.review.application.action;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.enums.ProjectStatus;
import com.company.scopery.modules.project.project.domain.model.*;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.clientportal.review.application.command.CreateClientReviewRequestCommand; import com.company.scopery.modules.clientportal.review.application.response.ClientReviewRequestResponse;
import com.company.scopery.modules.clientportal.review.domain.model.*; import com.company.scopery.modules.clientportal.shared.activity.ClientPortalActivityLogger;
import com.company.scopery.modules.clientportal.shared.authorization.ClientPortalAuthorizationService; import com.company.scopery.modules.clientportal.shared.constant.*;
import com.company.scopery.modules.clientportal.shared.error.ClientPortalExceptions;
import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional;
@Component
public class CreateClientReviewRequestAction {
    private final ProjectRepository projects; private final ClientReviewRequestRepository repo;
    private final ClientPortalAuthorizationService authorization; private final CurrentUserAuthorizationService currentUser; private final ClientPortalActivityLogger activityLogger;
    public CreateClientReviewRequestAction(ProjectRepository projects, ClientReviewRequestRepository repo, ClientPortalAuthorizationService authorization,
                                           CurrentUserAuthorizationService currentUser, ClientPortalActivityLogger activityLogger) {
        this.projects=projects; this.repo=repo; this.authorization=authorization; this.currentUser=currentUser; this.activityLogger=activityLogger;
    }
    @Transactional
    public ClientReviewRequestResponse execute(CreateClientReviewRequestCommand c) {
        authorization.requireManage(c.projectId());
        Project project = projects.findById(c.projectId()).orElseThrow(() -> ProjectExceptions.projectNotFound(c.projectId()));
        if (project.status() == ProjectStatus.ARCHIVED) throw ClientPortalExceptions.projectArchived(c.projectId());
        var actor = currentUser.resolveCurrentUser();
        var saved = repo.save(ClientReviewRequest.create(project.id(), project.workspaceId(), c.targetType(), c.targetId(), c.title().trim(), actor.id(), c.assignedPortalAccountId()));
        activityLogger.logSuccess(ClientPortalEntityTypes.REVIEW_REQUEST, saved.id(), ClientPortalActivityActions.REVIEW_CREATED, "Client review created");
        return ClientReviewRequestResponse.from(saved);
    }
}

package com.company.scopery.modules.projectbaseline.changerequest.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.enums.ChangeApprovalActionType;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.model.ChangeApprovalAction;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.model.ChangeApprovalActionRepository;
import com.company.scopery.modules.projectbaseline.changeimpact.domain.model.ChangeImpactRepository;
import com.company.scopery.modules.projectbaseline.changerequest.application.response.ChangeRequestResponse;
import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangeRequestStatus;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequest;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequestRepository;
import com.company.scopery.modules.projectbaseline.shared.activity.ProjectBaselineActivityLogger;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.*;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ApproveChangeRequestAction {
    private final ProjectRepository projects;
    private final ChangeRequestRepository changeRequests;
    private final ChangeImpactRepository impacts;
    private final ChangeApprovalActionRepository approvalActions;
    private final ProjectBaselineAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;

    public ApproveChangeRequestAction(ProjectRepository projects, ChangeRequestRepository changeRequests,
                                      ChangeImpactRepository impacts, ChangeApprovalActionRepository approvalActions,
                                      ProjectBaselineAuthorizationService authorization,
                                      CurrentUserAuthorizationService currentUser,
                                      ProjectBaselinePlatformPublisher publisher,
                                      ProjectBaselineActivityLogger activityLogger) {
        this.projects = projects; this.changeRequests = changeRequests; this.impacts = impacts;
        this.approvalActions = approvalActions; this.authorization = authorization; this.currentUser = currentUser;
        this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ChangeRequestResponse execute(UUID projectId, UUID changeRequestId) {
        authorization.requireChangeRequestApprove(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        Project project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ChangeRequest cr = changeRequests.findByIdAndProjectId(changeRequestId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.changeRequestNotFound(changeRequestId));
        if (cr.status() != ChangeRequestStatus.SUBMITTED) throw ProjectBaselineExceptions.changeRequestNotSubmitted(cr.id());
        var impact = impacts.findByChangeRequestId(cr.id());
        if (impact.isEmpty() || !impact.get().hasAnyImpact()) {
            throw ProjectBaselineExceptions.changeRequestImpactRequired(cr.id());
        }
        cr = changeRequests.save(cr.approve(actorId));
        approvalActions.save(ChangeApprovalAction.record(cr.id(), ChangeApprovalActionType.APPROVE, actorId, null, MDC.get("traceId")));
        publisher.enqueueChangeRequest(cr, ProjectBaselineEventCodes.CHANGE_REQUEST_APPROVED);
        publisher.audit(AuditEventType.CHANGE_REQUEST_APPROVED, actorId, project,
                ProjectBaselinePlatformPublisher.AGG_CHANGE_REQUEST, cr.id(), "ApproveChangeRequestAction");
        activityLogger.logSuccess(ProjectBaselineEntityTypes.CHANGE_REQUEST, cr.id(),
                ProjectBaselineActivityActions.CHANGE_REQUEST_APPROVED, "CHANGE_REQUEST_APPROVED");
        return ChangeRequestResponse.from(cr);
    }
}

package com.company.scopery.modules.projectbaseline.changerequest.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.enums.ChangeApprovalActionType;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.model.ChangeApprovalAction;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.model.ChangeApprovalActionRepository;
import com.company.scopery.modules.projectbaseline.changerequest.application.response.ChangeRequestResponse;
import com.company.scopery.modules.projectbaseline.changerequest.application.service.ApplyChangeRequestService;
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
public class ApplyChangeRequestAction {
    private final ProjectRepository projects;
    private final ChangeRequestRepository changeRequests;
    private final ChangeApprovalActionRepository approvalActions;
    private final ApplyChangeRequestService applyService;
    private final ProjectBaselineAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;

    public ApplyChangeRequestAction(ProjectRepository projects, ChangeRequestRepository changeRequests,
                                    ChangeApprovalActionRepository approvalActions,
                                    ApplyChangeRequestService applyService,
                                    ProjectBaselineAuthorizationService authorization,
                                    CurrentUserAuthorizationService currentUser,
                                    ProjectBaselinePlatformPublisher publisher,
                                    ProjectBaselineActivityLogger activityLogger) {
        this.projects = projects; this.changeRequests = changeRequests; this.approvalActions = approvalActions;
        this.applyService = applyService; this.authorization = authorization; this.currentUser = currentUser;
        this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ChangeRequestResponse execute(UUID projectId, UUID changeRequestId) {
        authorization.requireChangeRequestApply(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        Project project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        ChangeRequest cr = changeRequests.findByIdAndProjectId(changeRequestId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.changeRequestNotFound(changeRequestId));
        if (cr.status() == ChangeRequestStatus.APPLIED) throw ProjectBaselineExceptions.changeRequestAlreadyApplied(cr.id());
        if (cr.status() != ChangeRequestStatus.APPROVED) throw ProjectBaselineExceptions.changeRequestNotApproved(cr.id());
        try {
            applyService.applyAll(projectId, cr.id());
        } catch (RuntimeException e) {
            publisher.enqueueChangeRequest(cr, ProjectBaselineEventCodes.CHANGE_REQUEST_APPLY_FAILED);
            throw e;
        }
        cr = changeRequests.save(cr.apply(actorId));
        approvalActions.save(ChangeApprovalAction.record(cr.id(), ChangeApprovalActionType.APPLY, actorId, null, MDC.get("traceId")));
        publisher.enqueueChangeRequest(cr, ProjectBaselineEventCodes.CHANGE_REQUEST_APPLIED);
        publisher.audit(AuditEventType.CHANGE_REQUEST_APPLIED, actorId, project,
                ProjectBaselinePlatformPublisher.AGG_CHANGE_REQUEST, cr.id(), "ApplyChangeRequestAction");
        activityLogger.logSuccess(ProjectBaselineEntityTypes.CHANGE_REQUEST, cr.id(),
                ProjectBaselineActivityActions.CHANGE_REQUEST_APPLIED, "CHANGE_REQUEST_APPLIED");
        return ChangeRequestResponse.from(cr);
    }
}

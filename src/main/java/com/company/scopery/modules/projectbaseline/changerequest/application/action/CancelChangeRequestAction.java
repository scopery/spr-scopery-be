package com.company.scopery.modules.projectbaseline.changerequest.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.enums.ChangeApprovalActionType;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.model.ChangeApprovalAction;
import com.company.scopery.modules.projectbaseline.changeapproval.domain.model.ChangeApprovalActionRepository;
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
public class CancelChangeRequestAction {
    private final ChangeRequestRepository changeRequests;
    private final ChangeApprovalActionRepository approvalActions;
    private final ProjectBaselineAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;

    public CancelChangeRequestAction(ChangeRequestRepository changeRequests,
                                     ChangeApprovalActionRepository approvalActions,
                                     ProjectBaselineAuthorizationService authorization,
                                     CurrentUserAuthorizationService currentUser,
                                     ProjectBaselinePlatformPublisher publisher,
                                     ProjectBaselineActivityLogger activityLogger) {
        this.changeRequests = changeRequests; this.approvalActions = approvalActions;
        this.authorization = authorization; this.currentUser = currentUser;
        this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ChangeRequestResponse execute(UUID projectId, UUID changeRequestId) {
        authorization.requireChangeRequestCancel(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        ChangeRequest cr = changeRequests.findByIdAndProjectId(changeRequestId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.changeRequestNotFound(changeRequestId));
        if (cr.status() != ChangeRequestStatus.DRAFT && cr.status() != ChangeRequestStatus.SUBMITTED) {
            throw ProjectBaselineExceptions.changeRequestInvalidStatus(cr.id(), "cancel");
        }
        cr = changeRequests.save(cr.cancel(actorId));
        approvalActions.save(ChangeApprovalAction.record(cr.id(), ChangeApprovalActionType.CANCEL, actorId, null, MDC.get("traceId")));
        publisher.enqueueChangeRequest(cr, ProjectBaselineEventCodes.CHANGE_REQUEST_CANCELLED);
        activityLogger.logSuccess(ProjectBaselineEntityTypes.CHANGE_REQUEST, cr.id(),
                ProjectBaselineActivityActions.CHANGE_REQUEST_CANCELLED, "CHANGE_REQUEST_CANCELLED");
        return ChangeRequestResponse.from(cr);
    }
}

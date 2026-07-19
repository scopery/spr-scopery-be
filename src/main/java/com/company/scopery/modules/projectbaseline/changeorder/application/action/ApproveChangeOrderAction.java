package com.company.scopery.modules.projectbaseline.changeorder.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectbaseline.changeorder.application.response.ChangeOrderResponse;
import com.company.scopery.modules.projectbaseline.changeorder.domain.enums.ChangeOrderStatus;
import com.company.scopery.modules.projectbaseline.changeorder.domain.model.ChangeOrderRepository;
import com.company.scopery.modules.projectbaseline.shared.activity.ProjectBaselineActivityLogger;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.*;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ApproveChangeOrderAction {
    private final ProjectRepository projects;
    private final ChangeOrderRepository changeOrders;
    private final ProjectBaselineAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;

    public ApproveChangeOrderAction(ProjectRepository projects, ChangeOrderRepository changeOrders,
                                    ProjectBaselineAuthorizationService authorization,
                                    CurrentUserAuthorizationService currentUser,
                                    ProjectBaselinePlatformPublisher publisher,
                                    ProjectBaselineActivityLogger activityLogger) {
        this.projects = projects; this.changeOrders = changeOrders; this.authorization = authorization;
        this.currentUser = currentUser; this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ChangeOrderResponse execute(UUID projectId, UUID changeOrderId) {
        authorization.requireChangeOrderApprove(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        var order = changeOrders.findByIdAndProjectId(changeOrderId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.changeOrderNotFound(changeOrderId));
        if (order.status() != ChangeOrderStatus.DRAFT && order.status() != ChangeOrderStatus.SUBMITTED) {
            throw ProjectBaselineExceptions.changeOrderNotApprovable(order.id());
        }
        order = changeOrders.save(order.approve(actorId));
        publisher.enqueueChangeOrder(order, ProjectBaselineEventCodes.CHANGE_ORDER_APPROVED);
        publisher.audit(AuditEventType.CHANGE_ORDER_APPROVED, actorId, project,
                ProjectBaselinePlatformPublisher.AGG_CHANGE_ORDER, order.id(), "ApproveChangeOrderAction");
        activityLogger.logSuccess(ProjectBaselineEntityTypes.CHANGE_ORDER, order.id(),
                ProjectBaselineActivityActions.CHANGE_ORDER_APPROVED, "CHANGE_ORDER_APPROVED");
        return ChangeOrderResponse.from(order);
    }
}

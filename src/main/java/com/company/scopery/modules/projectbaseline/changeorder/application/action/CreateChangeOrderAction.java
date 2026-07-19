package com.company.scopery.modules.projectbaseline.changeorder.application.action;

import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.project.shared.error.ProjectExceptions;
import com.company.scopery.modules.projectbaseline.changeorder.application.response.ChangeOrderResponse;
import com.company.scopery.modules.projectbaseline.changeorder.domain.model.ChangeOrder;
import com.company.scopery.modules.projectbaseline.changeorder.domain.model.ChangeOrderRepository;
import com.company.scopery.modules.projectbaseline.changeorder.http.request.CreateChangeOrderRequest;
import com.company.scopery.modules.projectbaseline.changerequest.domain.enums.ChangeRequestStatus;
import com.company.scopery.modules.projectbaseline.changerequest.domain.model.ChangeRequestRepository;
import com.company.scopery.modules.projectbaseline.shared.activity.ProjectBaselineActivityLogger;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.*;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateChangeOrderAction {
    private final ProjectRepository projects;
    private final ChangeRequestRepository changeRequests;
    private final ChangeOrderRepository changeOrders;
    private final ProjectBaselineAuthorizationService authorization;
    private final ProjectBaselinePlatformPublisher publisher;
    private final ProjectBaselineActivityLogger activityLogger;

    public CreateChangeOrderAction(ProjectRepository projects, ChangeRequestRepository changeRequests,
                                   ChangeOrderRepository changeOrders,
                                   ProjectBaselineAuthorizationService authorization,
                                   ProjectBaselinePlatformPublisher publisher,
                                   ProjectBaselineActivityLogger activityLogger) {
        this.projects = projects; this.changeRequests = changeRequests; this.changeOrders = changeOrders;
        this.authorization = authorization; this.publisher = publisher; this.activityLogger = activityLogger;
    }

    @Transactional
    public ChangeOrderResponse execute(UUID projectId, UUID changeRequestId, CreateChangeOrderRequest req) {
        authorization.requireChangeOrderCreate(projectId);
        var project = projects.findById(projectId).orElseThrow(() -> ProjectExceptions.projectNotFound(projectId));
        var cr = changeRequests.findByIdAndProjectId(changeRequestId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.changeRequestNotFound(changeRequestId));
        if (cr.status() != ChangeRequestStatus.APPROVED && cr.status() != ChangeRequestStatus.APPLIED) {
            throw ProjectBaselineExceptions.changeOrderInvalidSource(cr.id());
        }
        if (changeOrders.existsByProjectIdAndCode(projectId, req.code())) {
            throw ProjectBaselineExceptions.changeOrderCodeExists(req.code());
        }
        ChangeOrder order = ChangeOrder.create(cr.id(), projectId, project.workspaceId(), req.code(), req.title(),
                req.description(), req.commercialImpactJson(), req.sourceQuoteVersionId());
        order = changeOrders.save(order);
        publisher.enqueueChangeOrder(order, ProjectBaselineEventCodes.CHANGE_ORDER_CREATED);
        activityLogger.logSuccess(ProjectBaselineEntityTypes.CHANGE_ORDER, order.id(),
                ProjectBaselineActivityActions.CHANGE_ORDER_CREATED, "CHANGE_ORDER_CREATED");
        return ChangeOrderResponse.from(order);
    }
}

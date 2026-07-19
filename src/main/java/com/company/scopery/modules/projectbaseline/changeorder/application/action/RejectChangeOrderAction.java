package com.company.scopery.modules.projectbaseline.changeorder.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.projectbaseline.changeorder.application.response.ChangeOrderResponse;
import com.company.scopery.modules.projectbaseline.changeorder.domain.model.ChangeOrderRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineEventCodes;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RejectChangeOrderAction {
    private final ChangeOrderRepository changeOrders;
    private final ProjectBaselineAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectBaselinePlatformPublisher publisher;

    public RejectChangeOrderAction(ChangeOrderRepository changeOrders,
                                   ProjectBaselineAuthorizationService authorization,
                                   CurrentUserAuthorizationService currentUser,
                                   ProjectBaselinePlatformPublisher publisher) {
        this.changeOrders = changeOrders; this.authorization = authorization;
        this.currentUser = currentUser; this.publisher = publisher;
    }

    @Transactional
    public ChangeOrderResponse execute(UUID projectId, UUID changeOrderId, String reason) {
        authorization.requireChangeOrderReject(projectId);
        if (reason == null || reason.isBlank()) throw ProjectBaselineExceptions.rejectionReasonRequired();
        UUID actorId = currentUser.resolveCurrentUser().id();
        var order = changeOrders.findByIdAndProjectId(changeOrderId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.changeOrderNotFound(changeOrderId));
        if (!order.isDraft()) throw ProjectBaselineExceptions.changeOrderNotDraft(order.id());
        order = changeOrders.save(order.reject(actorId, reason));
        publisher.enqueueChangeOrder(order, ProjectBaselineEventCodes.CHANGE_ORDER_REJECTED);
        return ChangeOrderResponse.from(order);
    }
}

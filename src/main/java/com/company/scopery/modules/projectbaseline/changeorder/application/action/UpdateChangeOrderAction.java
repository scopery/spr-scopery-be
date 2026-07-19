package com.company.scopery.modules.projectbaseline.changeorder.application.action;

import com.company.scopery.modules.projectbaseline.changeorder.application.response.ChangeOrderResponse;
import com.company.scopery.modules.projectbaseline.changeorder.domain.model.ChangeOrderRepository;
import com.company.scopery.modules.projectbaseline.changeorder.http.request.UpdateChangeOrderRequest;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.constant.ProjectBaselineEventCodes;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import com.company.scopery.modules.projectbaseline.shared.support.ProjectBaselinePlatformPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateChangeOrderAction {
    private final ChangeOrderRepository changeOrders;
    private final ProjectBaselineAuthorizationService authorization;
    private final ProjectBaselinePlatformPublisher publisher;

    public UpdateChangeOrderAction(ChangeOrderRepository changeOrders,
                                   ProjectBaselineAuthorizationService authorization,
                                   ProjectBaselinePlatformPublisher publisher) {
        this.changeOrders = changeOrders; this.authorization = authorization; this.publisher = publisher;
    }

    @Transactional
    public ChangeOrderResponse execute(UUID projectId, UUID changeOrderId, UpdateChangeOrderRequest req) {
        authorization.requireChangeOrderUpdate(projectId);
        var order = changeOrders.findByIdAndProjectId(changeOrderId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.changeOrderNotFound(changeOrderId));
        if (!order.isDraft()) throw ProjectBaselineExceptions.changeOrderNotDraft(order.id());
        order = changeOrders.save(order.updateDraft(req.title(), req.description(), req.commercialImpactJson(), req.sourceQuoteVersionId()));
        publisher.enqueueChangeOrder(order, ProjectBaselineEventCodes.CHANGE_ORDER_UPDATED);
        return ChangeOrderResponse.from(order);
    }
}

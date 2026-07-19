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
public class ArchiveChangeOrderAction {
    private final ChangeOrderRepository changeOrders;
    private final ProjectBaselineAuthorizationService authorization;
    private final CurrentUserAuthorizationService currentUser;
    private final ProjectBaselinePlatformPublisher publisher;

    public ArchiveChangeOrderAction(ChangeOrderRepository changeOrders,
                                    ProjectBaselineAuthorizationService authorization,
                                    CurrentUserAuthorizationService currentUser,
                                    ProjectBaselinePlatformPublisher publisher) {
        this.changeOrders = changeOrders; this.authorization = authorization;
        this.currentUser = currentUser; this.publisher = publisher;
    }

    @Transactional
    public ChangeOrderResponse execute(UUID projectId, UUID changeOrderId) {
        authorization.requireChangeOrderArchive(projectId);
        UUID actorId = currentUser.resolveCurrentUser().id();
        var order = changeOrders.findByIdAndProjectId(changeOrderId, projectId)
                .orElseThrow(() -> ProjectBaselineExceptions.changeOrderNotFound(changeOrderId));
        order = changeOrders.save(order.archive(actorId));
        publisher.enqueueChangeOrder(order, ProjectBaselineEventCodes.CHANGE_ORDER_ARCHIVED);
        return ChangeOrderResponse.from(order);
    }
}

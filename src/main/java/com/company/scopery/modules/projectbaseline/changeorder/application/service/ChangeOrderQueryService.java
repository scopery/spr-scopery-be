package com.company.scopery.modules.projectbaseline.changeorder.application.service;

import com.company.scopery.modules.projectbaseline.changeorder.application.response.ChangeOrderResponse;
import com.company.scopery.modules.projectbaseline.changeorder.domain.model.ChangeOrderRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChangeOrderQueryService {
    private final ChangeOrderRepository changeOrders;
    private final ProjectBaselineAuthorizationService authorization;

    public ChangeOrderQueryService(ChangeOrderRepository changeOrders,
                                   ProjectBaselineAuthorizationService authorization) {
        this.changeOrders = changeOrders; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ChangeOrderResponse> listByChangeRequest(UUID projectId, UUID changeRequestId) {
        authorization.requireChangeOrderView(projectId);
        return changeOrders.findByChangeRequestId(changeRequestId).stream().map(ChangeOrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ChangeOrderResponse get(UUID projectId, UUID changeOrderId) {
        authorization.requireChangeOrderView(projectId);
        return changeOrders.findByIdAndProjectId(changeOrderId, projectId)
                .map(ChangeOrderResponse::from)
                .orElseThrow(() -> ProjectBaselineExceptions.changeOrderNotFound(changeOrderId));
    }
}

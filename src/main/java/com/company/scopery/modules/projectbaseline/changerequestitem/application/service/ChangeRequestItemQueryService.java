package com.company.scopery.modules.projectbaseline.changerequestitem.application.service;

import com.company.scopery.modules.projectbaseline.changerequestitem.application.response.ChangeRequestItemResponse;
import com.company.scopery.modules.projectbaseline.changerequestitem.domain.model.ChangeRequestItemRepository;
import com.company.scopery.modules.projectbaseline.shared.authorization.ProjectBaselineAuthorizationService;
import com.company.scopery.modules.projectbaseline.shared.error.ProjectBaselineExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChangeRequestItemQueryService {
    private final ChangeRequestItemRepository items;
    private final ProjectBaselineAuthorizationService authorization;

    public ChangeRequestItemQueryService(ChangeRequestItemRepository items,
                                         ProjectBaselineAuthorizationService authorization) {
        this.items = items; this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<ChangeRequestItemResponse> list(UUID projectId, UUID changeRequestId) {
        authorization.requireItemView(projectId);
        return items.findByChangeRequestId(changeRequestId).stream().map(ChangeRequestItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ChangeRequestItemResponse get(UUID projectId, UUID changeRequestId, UUID itemId) {
        authorization.requireItemView(projectId);
        return items.findByIdAndChangeRequestId(itemId, changeRequestId)
                .map(ChangeRequestItemResponse::from)
                .orElseThrow(() -> ProjectBaselineExceptions.itemNotFound(itemId));
    }
}

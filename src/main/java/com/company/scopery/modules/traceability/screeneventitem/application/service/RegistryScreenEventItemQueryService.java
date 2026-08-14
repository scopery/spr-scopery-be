package com.company.scopery.modules.traceability.screeneventitem.application.service;

import com.company.scopery.modules.traceability.screeneventitem.application.response.RegistryScreenEventItemResponse;
import com.company.scopery.modules.traceability.screeneventitem.domain.enums.RegistryScreenEventItemStatus;
import com.company.scopery.modules.traceability.screeneventitem.domain.model.RegistryScreenEventItemRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistryScreenEventItemQueryService {

    private final RegistryScreenEventItemRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistryScreenEventItemQueryService(RegistryScreenEventItemRepository repo,
                                               TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryScreenEventItemResponse> list(UUID workspaceId, UUID screenId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByScreenIdAndStatusOrderByDisplayOrderAsc(screenId, RegistryScreenEventItemStatus.ACTIVE.name())
                .stream().map(RegistryScreenEventItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RegistryScreenEventItemResponse get(UUID workspaceId, UUID screenId, UUID eventItemId) {
        authorization.requireWorkspaceView(workspaceId);
        var item = repo.findByIdAndWorkspaceId(eventItemId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.screenEventItemNotFound(eventItemId));
        if (!item.screenId().equals(screenId)) {
            throw TraceabilityExceptions.screenEventItemNotFound(eventItemId);
        }
        return RegistryScreenEventItemResponse.from(item);
    }
}

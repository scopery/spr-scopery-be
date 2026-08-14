package com.company.scopery.modules.traceability.screenprocessitem.application.service;

import com.company.scopery.modules.traceability.screenprocessitem.application.response.RegistryScreenProcessItemResponse;
import com.company.scopery.modules.traceability.screenprocessitem.domain.enums.RegistryScreenProcessItemStatus;
import com.company.scopery.modules.traceability.screenprocessitem.domain.model.RegistryScreenProcessItemRepository;
import com.company.scopery.modules.traceability.shared.authorization.TraceabilityAuthorizationService;
import com.company.scopery.modules.traceability.shared.error.TraceabilityExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RegistryScreenProcessItemQueryService {

    private final RegistryScreenProcessItemRepository repo;
    private final TraceabilityAuthorizationService authorization;

    public RegistryScreenProcessItemQueryService(RegistryScreenProcessItemRepository repo,
                                                 TraceabilityAuthorizationService authorization) {
        this.repo = repo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<RegistryScreenProcessItemResponse> list(UUID workspaceId, UUID screenId) {
        authorization.requireWorkspaceView(workspaceId);
        return repo.findByScreenIdAndStatusOrderByDisplayOrderAsc(screenId, RegistryScreenProcessItemStatus.ACTIVE.name())
                .stream().map(RegistryScreenProcessItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public RegistryScreenProcessItemResponse get(UUID workspaceId, UUID screenId, UUID processItemId) {
        authorization.requireWorkspaceView(workspaceId);
        var item = repo.findByIdAndWorkspaceId(processItemId, workspaceId)
                .orElseThrow(() -> TraceabilityExceptions.screenProcessItemNotFound(processItemId));
        if (!item.screenId().equals(screenId)) {
            throw TraceabilityExceptions.screenProcessItemNotFound(processItemId);
        }
        return RegistryScreenProcessItemResponse.from(item);
    }
}

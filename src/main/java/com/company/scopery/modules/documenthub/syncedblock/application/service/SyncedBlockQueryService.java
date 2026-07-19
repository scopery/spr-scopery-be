package com.company.scopery.modules.documenthub.syncedblock.application.service;

import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.syncedblock.application.response.SyncedBlockResponse;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SyncedBlockQueryService {

    private final SyncedBlockRepository syncedBlockRepo;
    private final DocumentHubAuthorizationService authorization;

    public SyncedBlockQueryService(SyncedBlockRepository syncedBlockRepo,
                                    DocumentHubAuthorizationService authorization) {
        this.syncedBlockRepo = syncedBlockRepo;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public List<SyncedBlockResponse> listByWorkspace(UUID workspaceId) {
        authorization.requireWorkspaceView(workspaceId);
        return syncedBlockRepo.findByWorkspaceId(workspaceId).stream()
                .map(SyncedBlockResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SyncedBlockResponse get(UUID workspaceId, UUID syncedBlockId) {
        authorization.requireWorkspaceView(workspaceId);
        return syncedBlockRepo.findById(syncedBlockId)
                .map(SyncedBlockResponse::from)
                .orElseThrow(() -> DocumentHubExceptions.syncedBlockNotFound(syncedBlockId));
    }
}

package com.company.scopery.modules.documenthub.syncedblock.application.action;

import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.syncedblock.application.response.SyncedBlockResponse;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveSyncedBlockAction {

    private final SyncedBlockRepository syncedBlockRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;

    public ArchiveSyncedBlockAction(SyncedBlockRepository syncedBlockRepo,
                                     DocumentHubAuthorizationService authorization,
                                     DocumentHubActivityLogger activityLogger) {
        this.syncedBlockRepo = syncedBlockRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public SyncedBlockResponse execute(UUID workspaceId, UUID syncedBlockId) {
        var block = syncedBlockRepo.findById(syncedBlockId)
                .orElseThrow(() -> DocumentHubExceptions.syncedBlockNotFound(syncedBlockId));

        authorization.requireUpdate(block.projectId());

        var archived = syncedBlockRepo.save(block.archive());

        activityLogger.logSuccess(DocumentHubEntityTypes.SYNCED_BLOCK, archived.id(),
                DocumentHubActivityActions.SYNCED_BLOCK_ARCHIVED, "Synced block archived");

        return SyncedBlockResponse.from(archived);
    }
}

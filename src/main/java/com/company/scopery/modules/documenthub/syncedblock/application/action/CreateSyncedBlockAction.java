package com.company.scopery.modules.documenthub.syncedblock.application.action;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubOutboxEventCodes;
import com.company.scopery.modules.documenthub.syncedblock.application.command.CreateSyncedBlockCommand;
import com.company.scopery.modules.documenthub.syncedblock.application.response.SyncedBlockResponse;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlock;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRepository;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRevision;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRevisionRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
public class CreateSyncedBlockAction {

    private final SyncedBlockRepository syncedBlockRepo;
    private final SyncedBlockRevisionRepository revisionRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    private final TransactionalOutboxService outbox;

    public CreateSyncedBlockAction(SyncedBlockRepository syncedBlockRepo,
                                    SyncedBlockRevisionRepository revisionRepo,
                                    DocumentHubAuthorizationService authorization,
                                    DocumentHubActivityLogger activityLogger,
                                    TransactionalOutboxService outbox) {
        this.syncedBlockRepo = syncedBlockRepo;
        this.revisionRepo = revisionRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.outbox = outbox;
    }

    @Transactional
    public SyncedBlockResponse execute(CreateSyncedBlockCommand c) {
        authorization.requireCreate(c.projectId());

        var block = syncedBlockRepo.save(SyncedBlock.create(c.workspaceId(), c.projectId(), c.title(), c.schemaVersion()));

        var revision = SyncedBlockRevision.create(block.id(), 1L, c.ast(), resolveActorId());
        revisionRepo.save(revision);

        var saved = syncedBlockRepo.save(block.withRevision(1L));

        outbox.enqueue(DocumentHubEntityTypes.SYNCED_BLOCK, saved.id(),
                DocumentHubOutboxEventCodes.SYNCED_BLOCK_UPDATED,
                Map.of("syncedBlockId", saved.id(), "revisionNo", 1L));

        activityLogger.logSuccess(DocumentHubEntityTypes.SYNCED_BLOCK, saved.id(),
                DocumentHubActivityActions.SYNCED_BLOCK_CREATED, "Synced block created");

        return SyncedBlockResponse.from(saved);
    }

    private String resolveActorId() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof String userId) {
                return userId;
            }
        } catch (Exception ignored) {}
        return null;
    }
}

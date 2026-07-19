package com.company.scopery.modules.documenthub.syncedblock.application.action;

import com.company.scopery.modules.documenthub.shared.activity.DocumentHubActivityLogger;
import com.company.scopery.modules.documenthub.shared.authorization.DocumentHubAuthorizationService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubActivityActions;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.syncedblock.application.command.UpdateSyncedBlockCommand;
import com.company.scopery.modules.documenthub.syncedblock.application.response.SyncedBlockResponse;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRepository;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRevision;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRevisionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateSyncedBlockAction {

    private final SyncedBlockRepository syncedBlockRepo;
    private final SyncedBlockRevisionRepository revisionRepo;
    private final DocumentHubAuthorizationService authorization;
    private final DocumentHubActivityLogger activityLogger;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateSyncedBlockAction(SyncedBlockRepository syncedBlockRepo,
                                    SyncedBlockRevisionRepository revisionRepo,
                                    DocumentHubAuthorizationService authorization,
                                    DocumentHubActivityLogger activityLogger,
                                    ApplicationEventPublisher eventPublisher) {
        this.syncedBlockRepo = syncedBlockRepo;
        this.revisionRepo = revisionRepo;
        this.authorization = authorization;
        this.activityLogger = activityLogger;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public SyncedBlockResponse execute(UpdateSyncedBlockCommand c) {
        var block = syncedBlockRepo.findById(c.syncedBlockId())
                .orElseThrow(() -> DocumentHubExceptions.syncedBlockNotFound(c.syncedBlockId()));

        authorization.requireUpdate(block.projectId());

        long newRevisionNo = block.currentRevisionNo() + 1;
        revisionRepo.save(SyncedBlockRevision.create(block.id(), newRevisionNo, c.ast(), resolveActorId()));

        var saved = syncedBlockRepo.save(block.withRevision(newRevisionNo));

        activityLogger.logSuccess(DocumentHubEntityTypes.SYNCED_BLOCK, saved.id(),
                DocumentHubActivityActions.SYNCED_BLOCK_UPDATED, "Synced block updated, revision " + newRevisionNo);

        eventPublisher.publishEvent(new SyncedBlockUpdatedEvent(saved.id(), c.ast()));

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

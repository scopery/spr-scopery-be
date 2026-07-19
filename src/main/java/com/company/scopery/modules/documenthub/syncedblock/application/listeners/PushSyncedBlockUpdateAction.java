package com.company.scopery.modules.documenthub.syncedblock.application.listeners;

import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubEntityTypes;
import com.company.scopery.modules.documenthub.shared.constant.DocumentHubOutboxEventCodes;
import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.syncedblock.application.action.SyncedBlockUpdatedEvent;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockReferenceRepository;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
public class PushSyncedBlockUpdateAction {

    private static final Logger log = LoggerFactory.getLogger(PushSyncedBlockUpdateAction.class);
    private static final int MAX_PROPAGATION_DEPTH = 5;

    private final SyncedBlockRepository syncedBlockRepo;
    private final SyncedBlockReferenceRepository referenceRepo;
    private final TransactionalOutboxService outbox;

    public PushSyncedBlockUpdateAction(SyncedBlockRepository syncedBlockRepo,
                                        SyncedBlockReferenceRepository referenceRepo,
                                        TransactionalOutboxService outbox) {
        this.syncedBlockRepo = syncedBlockRepo;
        this.referenceRepo = referenceRepo;
        this.outbox = outbox;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onSyncedBlockUpdated(SyncedBlockUpdatedEvent event) {
        push(event, 0);
    }

    private void push(SyncedBlockUpdatedEvent event, int depth) {
        if (depth >= MAX_PROPAGATION_DEPTH) {
            throw DocumentHubExceptions.syncedBlockCycleDetected(event.syncedBlockId());
        }

        var block = syncedBlockRepo.findById(event.syncedBlockId()).orElse(null);
        if (block == null) return;

        var references = referenceRepo.findBySyncedBlockId(event.syncedBlockId());

        for (var ref : references) {
            try {
                outbox.enqueue(DocumentHubEntityTypes.SYNCED_BLOCK, event.syncedBlockId(),
                        DocumentHubOutboxEventCodes.SYNCED_BLOCK_UPDATED,
                        Map.of("syncedBlockId", event.syncedBlockId(),
                                "documentId", ref.documentId(),
                                "revisionNo", block.currentRevisionNo()));
            } catch (Exception e) {
                log.warn("Failed to push synced block update to document {}: {}", ref.documentId(), e.getMessage());
            }
        }
    }
}

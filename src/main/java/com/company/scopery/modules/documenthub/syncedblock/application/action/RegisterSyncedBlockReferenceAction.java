package com.company.scopery.modules.documenthub.syncedblock.application.action;

import com.company.scopery.modules.documenthub.shared.error.DocumentHubExceptions;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockReference;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockReferenceRepository;
import com.company.scopery.modules.documenthub.syncedblock.domain.model.SyncedBlockRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RegisterSyncedBlockReferenceAction {

    private final SyncedBlockRepository syncedBlockRepo;
    private final SyncedBlockReferenceRepository referenceRepo;

    public RegisterSyncedBlockReferenceAction(SyncedBlockRepository syncedBlockRepo,
                                               SyncedBlockReferenceRepository referenceRepo) {
        this.syncedBlockRepo = syncedBlockRepo;
        this.referenceRepo = referenceRepo;
    }

    @Transactional
    public void execute(UUID syncedBlockId, UUID documentId) {
        syncedBlockRepo.findById(syncedBlockId)
                .orElseThrow(() -> DocumentHubExceptions.syncedBlockNotFound(syncedBlockId));

        referenceRepo.findBySyncedBlockIdAndDocumentId(syncedBlockId, documentId)
                .ifPresentOrElse(
                        existing -> {},
                        () -> referenceRepo.save(SyncedBlockReference.create(syncedBlockId, documentId))
                );
    }

    @Transactional
    public void deregister(UUID syncedBlockId, UUID documentId) {
        referenceRepo.deleteByDocumentIdAndSyncedBlockId(documentId, syncedBlockId);
    }
}

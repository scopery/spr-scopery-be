package com.company.scopery.modules.specpack.block.application.action;

import com.company.scopery.modules.specpack.block.application.response.BlockResponse;
import com.company.scopery.modules.specpack.block.domain.enums.ChangeSource;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlock;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRepository;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRevision;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RestoreBlockRevisionAction {

    private final SpecPackBlockRepository repository;
    private final SpecPackActivityLogger activityLogger;

    public RestoreBlockRevisionAction(SpecPackBlockRepository repository, SpecPackActivityLogger activityLogger) {
        this.repository = repository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public BlockResponse execute(UUID projectId, UUID specPackId, UUID blockId, int revisionNumber) {
        SpecPackBlock block = repository.findById(blockId)
                .orElseThrow(() -> SpecPackExceptions.blockNotFound(blockId));
        if (!block.specPackId().equals(specPackId)) {
            throw SpecPackExceptions.blockNotFound(blockId);
        }
        if (block.isDeleted()) {
            throw SpecPackExceptions.blockDeleted(blockId);
        }

        SpecPackBlockRevision target = repository.findRevisionByBlockIdAndNumber(blockId, revisionNumber)
                .orElseThrow(() -> SpecPackExceptions.blockRevisionNotFound(blockId, revisionNumber));

        block.restoreRevision(target);
        SpecPackBlock saved = repository.save(block);

        SpecPackBlockRevision newRevision = SpecPackBlockRevision.create(
                saved.id(), saved.currentRevisionNumber(), saved.title(),
                saved.contentFormat(), saved.contentJson(), saved.sourceRefsJson(),
                ChangeSource.RESTORE, "Restored from revision " + revisionNumber
        );
        repository.saveRevision(newRevision);

        activityLogger.logSuccess(SpecPackEntityTypes.SPEC_PACK_BLOCK, saved.id(),
                SpecPackActivityActions.BLOCK_REVISION_RESTORED,
                "Block " + saved.blockKey() + " restored to revision " + revisionNumber);

        return BlockResponse.from(saved);
    }
}

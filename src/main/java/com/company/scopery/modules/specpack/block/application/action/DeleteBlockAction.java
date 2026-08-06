package com.company.scopery.modules.specpack.block.application.action;

import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlock;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeleteBlockAction {

    private final SpecPackBlockRepository repository;
    private final SpecPackActivityLogger activityLogger;

    public DeleteBlockAction(SpecPackBlockRepository repository, SpecPackActivityLogger activityLogger) {
        this.repository = repository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(UUID projectId, UUID specPackId, UUID blockId) {
        SpecPackBlock block = repository.findById(blockId)
                .orElseThrow(() -> SpecPackExceptions.blockNotFound(blockId));
        if (!block.specPackId().equals(specPackId)) {
            throw SpecPackExceptions.blockNotFound(blockId);
        }
        if (block.isDeleted()) {
            throw SpecPackExceptions.blockDeleted(blockId);
        }

        block.softDelete();
        repository.save(block);

        activityLogger.logSuccess(SpecPackEntityTypes.SPEC_PACK_BLOCK, blockId,
                SpecPackActivityActions.BLOCK_DELETED, "Block deleted: " + block.blockKey());
    }
}

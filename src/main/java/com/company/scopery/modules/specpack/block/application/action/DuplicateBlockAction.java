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
public class DuplicateBlockAction {

    private final SpecPackBlockRepository repository;
    private final SpecPackActivityLogger activityLogger;

    public DuplicateBlockAction(SpecPackBlockRepository repository, SpecPackActivityLogger activityLogger) {
        this.repository = repository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public BlockResponse execute(UUID projectId, UUID specPackId, UUID blockId) {
        SpecPackBlock source = repository.findById(blockId)
                .orElseThrow(() -> SpecPackExceptions.blockNotFound(blockId));
        if (!source.specPackId().equals(specPackId)) {
            throw SpecPackExceptions.blockNotFound(blockId);
        }
        if (source.isDeleted()) {
            throw SpecPackExceptions.blockDeleted(blockId);
        }

        String copyKey = generateCopyKey(specPackId, source.blockKey());

        SpecPackBlock copy = SpecPackBlock.create(
                specPackId, copyKey, source.parentBlockId(),
                source.blockType(), source.title(), source.contentFormat(),
                source.contentJson(), source.sourceRefsJson(),
                source.displayOrder() + 1
        );

        SpecPackBlock saved = repository.save(copy);

        SpecPackBlockRevision revision = SpecPackBlockRevision.create(
                saved.id(), saved.currentRevisionNumber(), saved.title(),
                saved.contentFormat(), saved.contentJson(), saved.sourceRefsJson(),
                ChangeSource.MANUAL, "Duplicated from: " + source.blockKey()
        );
        repository.saveRevision(revision);

        activityLogger.logSuccess(SpecPackEntityTypes.SPEC_PACK_BLOCK, saved.id(),
                SpecPackActivityActions.BLOCK_DUPLICATED, "Block duplicated from " + source.blockKey() + " to " + copyKey);

        return BlockResponse.from(saved);
    }

    private String generateCopyKey(UUID specPackId, String originalKey) {
        String base = originalKey + ".copy";
        String candidate = base;
        int suffix = 1;
        while (repository.existsByPackIdAndBlockKey(specPackId, candidate)) {
            suffix++;
            candidate = base + "-" + suffix;
        }
        return candidate;
    }
}

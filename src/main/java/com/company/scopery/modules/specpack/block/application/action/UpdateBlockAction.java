package com.company.scopery.modules.specpack.block.application.action;

import com.company.scopery.modules.specpack.block.application.command.UpdateBlockCommand;
import com.company.scopery.modules.specpack.block.application.response.BlockResponse;
import com.company.scopery.modules.specpack.block.domain.enums.BlockType;
import com.company.scopery.modules.specpack.block.domain.enums.ChangeSource;
import com.company.scopery.modules.specpack.block.domain.enums.ContentFormat;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlock;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRepository;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRevision;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import com.company.scopery.modules.specpack.shared.util.SpecPackEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateBlockAction {

    private final SpecPackBlockRepository repository;
    private final SpecPackActivityLogger activityLogger;

    public UpdateBlockAction(SpecPackBlockRepository repository, SpecPackActivityLogger activityLogger) {
        this.repository = repository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public BlockResponse execute(UpdateBlockCommand command) {
        SpecPackBlock block = findOrThrow(command.blockId(), command.specPackId());
        ContentFormat contentFormat = SpecPackEnumParser.parseRequired(ContentFormat.class, command.contentFormat(), "contentFormat");

        if (block.currentRevisionNumber() != command.expectedRevisionNumber()) {
            throw SpecPackExceptions.blockStaleRevision(command.blockId(), command.expectedRevisionNumber(), block.currentRevisionNumber());
        }

        if (command.parentBlockId() != null) {
            SpecPackBlock parent = repository.findById(command.parentBlockId())
                    .orElseThrow(() -> SpecPackExceptions.blockParentNotFound(command.parentBlockId().toString()));
            if (parent.blockType() == BlockType.PAGE_BREAK) {
                throw SpecPackExceptions.blockPageBreakCannotBeParent();
            }
        }

        block.update(command.parentBlockId(), command.title(), contentFormat,
                command.contentJson(), command.sourceRefsJson(), command.expectedRevisionNumber());

        SpecPackBlock saved = repository.save(block);

        SpecPackBlockRevision revision = SpecPackBlockRevision.create(
                saved.id(), saved.currentRevisionNumber(), saved.title(),
                saved.contentFormat(), saved.contentJson(), saved.sourceRefsJson(),
                ChangeSource.MANUAL, null
        );
        repository.saveRevision(revision);

        activityLogger.logSuccess(SpecPackEntityTypes.SPEC_PACK_BLOCK, saved.id(),
                SpecPackActivityActions.BLOCK_UPDATED, "Block updated: " + saved.blockKey());

        return BlockResponse.from(saved);
    }

    private SpecPackBlock findOrThrow(java.util.UUID blockId, java.util.UUID specPackId) {
        SpecPackBlock block = repository.findById(blockId)
                .orElseThrow(() -> SpecPackExceptions.blockNotFound(blockId));
        if (!block.specPackId().equals(specPackId)) {
            throw SpecPackExceptions.blockNotFound(blockId);
        }
        if (block.isDeleted()) {
            throw SpecPackExceptions.blockDeleted(blockId);
        }
        return block;
    }
}

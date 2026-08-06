package com.company.scopery.modules.specpack.block.application.action;

import com.company.scopery.modules.specpack.block.application.command.CreateBlockCommand;
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
public class CreateBlockAction {

    private final SpecPackBlockRepository repository;
    private final SpecPackActivityLogger activityLogger;

    public CreateBlockAction(SpecPackBlockRepository repository, SpecPackActivityLogger activityLogger) {
        this.repository = repository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public BlockResponse execute(CreateBlockCommand command) {
        BlockType blockType = SpecPackEnumParser.parseRequired(BlockType.class, command.blockType(), "blockType");
        ContentFormat contentFormat = SpecPackEnumParser.parseRequired(ContentFormat.class, command.contentFormat(), "contentFormat");

        if (command.blockKey() != null && command.blockKey().contains(" ")) {
            throw SpecPackExceptions.blockKeyInvalid(command.blockKey());
        }

        if (repository.existsByPackIdAndBlockKey(command.specPackId(), command.blockKey())) {
            throw SpecPackExceptions.blockKeyExists(command.blockKey());
        }

        if (command.parentBlockId() != null) {
            SpecPackBlock parent = repository.findById(command.parentBlockId())
                    .orElseThrow(() -> SpecPackExceptions.blockParentNotFound(command.parentBlockId().toString()));
            if (parent.blockType() == BlockType.PAGE_BREAK) {
                throw SpecPackExceptions.blockPageBreakCannotBeParent();
            }
        }

        SpecPackBlock block = SpecPackBlock.create(
                command.specPackId(), command.blockKey(), command.parentBlockId(),
                blockType, command.title(), contentFormat,
                command.contentJson(), command.sourceRefsJson(), command.displayOrder()
        );

        SpecPackBlock saved = repository.save(block);

        SpecPackBlockRevision revision = SpecPackBlockRevision.create(
                saved.id(), saved.currentRevisionNumber(), saved.title(),
                saved.contentFormat(), saved.contentJson(), saved.sourceRefsJson(),
                ChangeSource.MANUAL, null
        );
        repository.saveRevision(revision);

        activityLogger.logSuccess(SpecPackEntityTypes.SPEC_PACK_BLOCK, saved.id(),
                SpecPackActivityActions.BLOCK_CREATED, "Block created: " + saved.blockKey());

        return BlockResponse.from(saved);
    }
}

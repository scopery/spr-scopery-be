package com.company.scopery.modules.specpack.block.application.action;

import com.company.scopery.modules.specpack.block.application.command.ReorderBlocksCommand;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlock;
import com.company.scopery.modules.specpack.block.domain.model.SpecPackBlockRepository;
import com.company.scopery.modules.specpack.shared.activity.SpecPackActivityLogger;
import com.company.scopery.modules.specpack.shared.constant.SpecPackActivityActions;
import com.company.scopery.modules.specpack.shared.constant.SpecPackEntityTypes;
import com.company.scopery.modules.specpack.shared.error.SpecPackExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ReorderBlocksAction {

    private final SpecPackBlockRepository repository;
    private final SpecPackActivityLogger activityLogger;

    public ReorderBlocksAction(SpecPackBlockRepository repository, SpecPackActivityLogger activityLogger) {
        this.repository = repository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(ReorderBlocksCommand command) {
        List<SpecPackBlock> updated = new ArrayList<>();
        for (ReorderBlocksCommand.BlockOrderItem item : command.orderedItems()) {
            SpecPackBlock block = repository.findById(item.blockId())
                    .orElseThrow(() -> SpecPackExceptions.blockNotFound(item.blockId()));
            if (!block.specPackId().equals(command.specPackId())) {
                throw SpecPackExceptions.blockNotFound(item.blockId());
            }
            block.setDisplayOrder(item.displayOrder());
            updated.add(block);
        }
        repository.saveAll(updated);

        activityLogger.logSuccess(SpecPackEntityTypes.SPEC_PACK, command.specPackId(),
                SpecPackActivityActions.BLOCKS_REORDERED,
                "Blocks reordered in pack: " + command.specPackId() + " (" + command.orderedItems().size() + " items)");
    }
}

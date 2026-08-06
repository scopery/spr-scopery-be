package com.company.scopery.modules.specpack.block.application.command;

import java.util.List;
import java.util.UUID;

public record ReorderBlocksCommand(
        UUID projectId,
        UUID specPackId,
        List<BlockOrderItem> orderedItems
) {
    public record BlockOrderItem(UUID blockId, int displayOrder) {}
}

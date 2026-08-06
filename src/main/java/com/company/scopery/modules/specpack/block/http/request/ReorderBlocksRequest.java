package com.company.scopery.modules.specpack.block.http.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderBlocksRequest(
        @NotEmpty List<BlockOrderItem> orderedItems
) {
    public record BlockOrderItem(
            UUID blockId,
            int displayOrder
    ) {}
}

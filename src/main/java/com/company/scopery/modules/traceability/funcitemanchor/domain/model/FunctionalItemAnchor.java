package com.company.scopery.modules.traceability.funcitemanchor.domain.model;

import com.company.scopery.modules.traceability.funcitemanchor.domain.enums.AnchorNodeType;

import java.time.Instant;
import java.util.UUID;

public record FunctionalItemAnchor(
        UUID id,
        UUID functionalItemId,
        AnchorNodeType nodeType,
        UUID nodeId,
        String note,
        Instant createdAt
) {
    public static FunctionalItemAnchor create(
            UUID functionalItemId,
            AnchorNodeType nodeType,
            UUID nodeId,
            String note
    ) {
        return new FunctionalItemAnchor(
                UUID.randomUUID(),
                functionalItemId,
                nodeType,
                nodeId,
                note,
                Instant.now()
        );
    }
}

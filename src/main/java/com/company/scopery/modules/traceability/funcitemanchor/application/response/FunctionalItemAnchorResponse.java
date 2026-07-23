package com.company.scopery.modules.traceability.funcitemanchor.application.response;

import com.company.scopery.modules.traceability.funcitemanchor.domain.model.FunctionalItemAnchor;

import java.time.Instant;
import java.util.UUID;

public record FunctionalItemAnchorResponse(
        UUID id,
        UUID functionalItemId,
        String nodeType,
        UUID nodeId,
        String note,
        Instant createdAt
) {
    public static FunctionalItemAnchorResponse from(FunctionalItemAnchor d) {
        return new FunctionalItemAnchorResponse(
                d.id(),
                d.functionalItemId(),
                d.nodeType() != null ? d.nodeType().name() : null,
                d.nodeId(),
                d.note(),
                d.createdAt()
        );
    }
}

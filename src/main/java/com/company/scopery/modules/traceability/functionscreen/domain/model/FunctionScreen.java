package com.company.scopery.modules.traceability.functionscreen.domain.model;

import java.time.Instant;
import java.util.UUID;

public record FunctionScreen(UUID functionId, UUID screenId, String note, Instant createdAt) {

    public static FunctionScreen create(UUID functionId, UUID screenId, String note) {
        return new FunctionScreen(functionId, screenId, note, Instant.now());
    }
}

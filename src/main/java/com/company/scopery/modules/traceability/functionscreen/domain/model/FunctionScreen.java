package com.company.scopery.modules.traceability.functionscreen.domain.model;

import java.time.Instant;
import java.util.UUID;

public record FunctionScreen(UUID functionId, UUID screenId, String note, String role, String modeCode, int displayOrder, Instant createdAt) {

    public static FunctionScreen create(UUID functionId, UUID screenId, String note, String role, String modeCode, int displayOrder) {
        return new FunctionScreen(functionId, screenId, note, role, modeCode, displayOrder, Instant.now());
    }
}

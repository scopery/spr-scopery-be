package com.company.scopery.modules.traceability.functionscreen.application.response;

import com.company.scopery.modules.traceability.functionscreen.domain.model.FunctionScreen;

import java.time.Instant;
import java.util.UUID;

public record FunctionScreenResponse(UUID functionId, UUID screenId, String note, String role, String modeCode, int displayOrder, Instant createdAt) {

    public static FunctionScreenResponse from(FunctionScreen d) {
        return new FunctionScreenResponse(d.functionId(), d.screenId(), d.note(), d.role(), d.modeCode(), d.displayOrder(), d.createdAt());
    }
}

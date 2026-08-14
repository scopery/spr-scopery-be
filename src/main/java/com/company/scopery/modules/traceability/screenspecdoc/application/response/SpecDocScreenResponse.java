package com.company.scopery.modules.traceability.screenspecdoc.application.response;

import com.company.scopery.modules.traceability.screenspecdoc.domain.model.SpecDocScreen;

import java.util.UUID;

public record SpecDocScreenResponse(
        UUID documentId,
        UUID screenId,
        int displayOrder,
        String note) {

    public static SpecDocScreenResponse from(SpecDocScreen s) {
        return new SpecDocScreenResponse(s.documentId(), s.screenId(), s.displayOrder(), s.note());
    }
}

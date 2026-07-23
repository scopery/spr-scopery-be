package com.company.scopery.modules.traceability.screencomponent.application.response;

import com.company.scopery.modules.traceability.screencomponent.domain.model.ScreenComponent;

import java.time.Instant;
import java.util.UUID;

public record ScreenComponentResponse(
        UUID screenId,
        UUID componentId,
        UUID sectionId,
        int displayOrder,
        String note,
        Instant createdAt) {

    public static ScreenComponentResponse from(ScreenComponent d) {
        return new ScreenComponentResponse(
                d.screenId(),
                d.componentId(),
                d.sectionId(),
                d.displayOrder(),
                d.note(),
                d.createdAt());
    }
}

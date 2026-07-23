package com.company.scopery.modules.traceability.screencomponent.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ScreenComponent(
        UUID screenId,
        UUID componentId,
        UUID sectionId,
        int displayOrder,
        String note,
        Instant createdAt) {

    public static ScreenComponent create(UUID screenId, UUID componentId, UUID sectionId,
                                         int displayOrder, String note) {
        return new ScreenComponent(screenId, componentId, sectionId, displayOrder, note, Instant.now());
    }
}

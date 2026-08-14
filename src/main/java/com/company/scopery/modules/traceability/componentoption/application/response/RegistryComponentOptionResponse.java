package com.company.scopery.modules.traceability.componentoption.application.response;

import com.company.scopery.modules.traceability.componentoption.domain.model.RegistryComponentOption;

import java.time.Instant;
import java.util.UUID;

public record RegistryComponentOptionResponse(
        UUID id,
        UUID componentId,
        UUID workspaceId,
        String optionValue,
        String optionLabel,
        int displayOrder,
        String status,
        Instant createdAt
) {
    public static RegistryComponentOptionResponse from(RegistryComponentOption o) {
        return new RegistryComponentOptionResponse(
                o.id(),
                o.componentId(),
                o.workspaceId(),
                o.optionValue(),
                o.optionLabel(),
                o.displayOrder(),
                o.status().name(),
                o.createdAt()
        );
    }
}

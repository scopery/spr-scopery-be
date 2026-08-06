package com.company.scopery.modules.specpack.pack.application.response;

import com.company.scopery.modules.specpack.pack.domain.model.SpecPack;

import java.time.Instant;
import java.util.UUID;

public record SpecPackResponse(
        UUID id,
        UUID projectId,
        String packType,
        String name,
        String description,
        String status,
        UUID currentVersionId,
        UUID sourcePackId,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt,
        Instant archivedAt
) {
    public static SpecPackResponse from(SpecPack pack) {
        return new SpecPackResponse(
                pack.id(),
                pack.projectId(),
                pack.packType().name(),
                pack.name(),
                pack.description(),
                pack.status().name(),
                pack.currentVersionId(),
                pack.sourcePackId(),
                pack.createdBy(),
                pack.createdAt(),
                pack.updatedBy(),
                pack.updatedAt(),
                pack.archivedAt()
        );
    }
}

package com.company.scopery.modules.traceability.specdocrevision.application.response;

import com.company.scopery.modules.traceability.specdocrevision.domain.model.RegistrySpecDocRevision;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrySpecDocRevisionResponse(
        UUID id,
        UUID documentId,
        UUID workspaceId,
        String revisionNo,
        String targetSheetName,
        String details,
        String personInCharge,
        String color,
        LocalDate changedAt,
        int displayOrder,
        String status,
        Instant createdAt) {

    public static RegistrySpecDocRevisionResponse from(RegistrySpecDocRevision r) {
        return new RegistrySpecDocRevisionResponse(
                r.id(),
                r.documentId(),
                r.workspaceId(),
                r.revisionNo(),
                r.targetSheetName(),
                r.details(),
                r.personInCharge(),
                r.color(),
                r.changedAt(),
                r.displayOrder(),
                r.status().name(),
                r.createdAt());
    }
}

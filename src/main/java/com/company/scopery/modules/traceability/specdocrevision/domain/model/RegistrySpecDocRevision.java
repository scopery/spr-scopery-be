package com.company.scopery.modules.traceability.specdocrevision.domain.model;

import com.company.scopery.modules.traceability.specdocrevision.domain.enums.RegistrySpecDocRevisionStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrySpecDocRevision(
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
        RegistrySpecDocRevisionStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static RegistrySpecDocRevision create(
            UUID documentId,
            UUID workspaceId,
            String revisionNo,
            String targetSheetName,
            String details,
            String personInCharge,
            String color,
            LocalDate changedAt,
            int displayOrder) {
        return new RegistrySpecDocRevision(
                UUID.randomUUID(),
                documentId,
                workspaceId,
                revisionNo,
                targetSheetName,
                details,
                personInCharge,
                color,
                changedAt,
                displayOrder,
                RegistrySpecDocRevisionStatus.ACTIVE,
                0,
                null,
                null);
    }

    public RegistrySpecDocRevision withUpdated(
            String revisionNo,
            String targetSheetName,
            String details,
            String personInCharge,
            String color,
            LocalDate changedAt,
            int displayOrder) {
        return new RegistrySpecDocRevision(
                id, documentId, workspaceId,
                revisionNo, targetSheetName, details, personInCharge, color,
                changedAt, displayOrder, status, version, createdAt, Instant.now());
    }
}

package com.company.scopery.modules.traceability.screenspecdoc.application.response;

import com.company.scopery.modules.traceability.screen.application.response.ScreenFullSpecResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SpecDocFullSpecResponse(
        UUID id,
        UUID projectId,
        UUID workspaceId,
        String documentCode,
        String documentName,
        String projectName,
        String systemName,
        String phaseName,
        String language,
        String overview,
        String figmaUrl,
        String status,
        Instant createdAt,
        List<RevisionEntry> revisions,
        List<ScreenSpecEntry> screens) {

    public record RevisionEntry(
            UUID id,
            String revisionNo,
            String targetSheetName,
            String details,
            String personInCharge,
            String color,
            LocalDate changedAt,
            int displayOrder) {}

    public record ScreenSpecEntry(
            UUID screenId,
            int displayOrder,
            String note,
            ScreenFullSpecResponse spec) {}
}

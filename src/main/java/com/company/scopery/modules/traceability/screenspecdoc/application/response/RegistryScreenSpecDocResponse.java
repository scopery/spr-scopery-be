package com.company.scopery.modules.traceability.screenspecdoc.application.response;

import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocument;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenSpecDocResponse(
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
        Instant createdAt) {

    public static RegistryScreenSpecDocResponse from(RegistryScreenSpecDocument d) {
        return new RegistryScreenSpecDocResponse(
                d.id(),
                d.projectId(),
                d.workspaceId(),
                d.documentCode(),
                d.documentName(),
                d.projectName(),
                d.systemName(),
                d.phaseName(),
                d.language(),
                d.overview(),
                d.figmaUrl(),
                d.status().name(),
                d.createdAt());
    }
}

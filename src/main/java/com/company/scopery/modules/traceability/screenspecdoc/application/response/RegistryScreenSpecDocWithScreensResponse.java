package com.company.scopery.modules.traceability.screenspecdoc.application.response;

import com.company.scopery.modules.traceability.screenspecdoc.domain.model.RegistryScreenSpecDocument;
import com.company.scopery.modules.traceability.screenspecdoc.domain.model.SpecDocScreen;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RegistryScreenSpecDocWithScreensResponse(
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
        List<SpecDocScreenResponse> screens) {

    public static RegistryScreenSpecDocWithScreensResponse from(RegistryScreenSpecDocument d,
                                                                List<SpecDocScreen> screens) {
        return new RegistryScreenSpecDocWithScreensResponse(
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
                d.createdAt(),
                screens.stream().map(SpecDocScreenResponse::from).toList());
    }
}

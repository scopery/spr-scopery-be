package com.company.scopery.modules.traceability.screenspecdoc.domain.model;

import com.company.scopery.modules.traceability.screenspecdoc.domain.enums.RegistryScreenSpecDocStatus;

import java.time.Instant;
import java.util.UUID;

public record RegistryScreenSpecDocument(
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
        RegistryScreenSpecDocStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static RegistryScreenSpecDocument create(
            UUID projectId,
            UUID workspaceId,
            String documentCode,
            String documentName,
            String projectName,
            String systemName,
            String phaseName,
            String language,
            String overview,
            String figmaUrl) {
        return new RegistryScreenSpecDocument(
                UUID.randomUUID(),
                projectId,
                workspaceId,
                documentCode,
                documentName,
                projectName,
                systemName,
                phaseName,
                language,
                overview,
                figmaUrl,
                RegistryScreenSpecDocStatus.ACTIVE,
                0,
                null,
                null);
    }

    public RegistryScreenSpecDocument withUpdated(
            String documentName,
            String projectName,
            String systemName,
            String phaseName,
            String language,
            String overview,
            String figmaUrl) {
        return new RegistryScreenSpecDocument(
                id, projectId, workspaceId, documentCode,
                documentName, projectName, systemName, phaseName,
                language, overview, figmaUrl,
                status, version, createdAt, Instant.now());
    }
}
